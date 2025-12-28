package org.plate.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.plate.common.enums.ErrorCode;
import org.plate.common.enums.TokenType;
import org.plate.common.exception.BusinessException;
import org.plate.dto.auth.request.LoginRequest;
import org.plate.dto.auth.request.SignUpRequest;
import org.plate.dto.auth.response.TokenResponse;
import org.plate.dto.auth.response.UserResponse;
import org.plate.entity.user.User;
import org.plate.security.jwt.JwtProvider;
import org.plate.security.jwt.TokenService;
import org.plate.security.userdetails.UserPrincipal;
import org.plate.service.user.UserService;
import org.plate.vo.password.HashedPassword;
import org.plate.vo.password.PlainPassword;
import org.plate.vo.token.TokenPair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 Facade
 * prj-core의 AuthFacade와 동일한 역할
 * 여러 Service를 조율하여 인증 관련 비즈니스 로직을 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    /**
     * 로그인 처리
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        // 비밀번호 검증
        HashedPassword hashedPassword = HashedPassword.fromHash(user.getPassword());
        if (!hashedPassword.matches(request.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        // 토큰 생성
        UserPrincipal userPrincipal = toUserPrincipal(user);
        TokenPair tokenPair = tokenService.generateTokenPair(userPrincipal);

        log.info("로그인 성공 - 사용자: {}", user.getEmail());

        return buildTokenResponse(tokenPair, user);
    }

    /**
     * 회원가입 처리
     */
    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        // 이메일 중복 확인
        if (userService.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_EMAIL_DUPLICATE);
        }

        // 비밀번호 암호화
        PlainPassword plainPassword = PlainPassword.of(request.getPassword());
        HashedPassword hashedPassword = plainPassword.toHashed();

        // 사용자 생성
        User newUser = User.create(
                request.getEmail(),
                hashedPassword.getValue(),
                request.getName(),
                request.getPhone()
        );

        User savedUser = userService.save(newUser);

        // 토큰 생성
        UserPrincipal userPrincipal = toUserPrincipal(savedUser);
        TokenPair tokenPair = tokenService.generateTokenPair(userPrincipal);

        log.info("회원가입 성공 - 사용자: {}", request.getEmail());

        return buildTokenResponse(tokenPair, savedUser);
    }

    /**
     * 토큰 갱신
     */
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(String refreshToken) {
        // 토큰 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
        }

        // Refresh Token 타입 확인
        if (jwtProvider.getTokenType(refreshToken) != TokenType.REFRESH) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
        }

        // 사용자 정보 추출
        UserPrincipal userPrincipal = jwtProvider.getUserPrincipalFromToken(refreshToken);

        // 사용자 조회 (최신 정보 반영)
        User user = userService.findActiveByEmail(userPrincipal.getEmail());

        // 새 토큰 발급
        UserPrincipal newUserPrincipal = toUserPrincipal(user);
        TokenPair tokenPair = tokenService.generateTokenPair(newUserPrincipal);

        log.info("토큰 갱신 성공 - 사용자: {}", user.getEmail());

        return buildTokenResponse(tokenPair, user);
    }

    /**
     * 토큰 검증
     */
    public UserResponse verifyToken(UserPrincipal userPrincipal) {
        // 사용자 조회하여 최신 정보 반환
        User user = userService.findActiveById(userPrincipal.getId());
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }

    /**
     * 로그아웃 처리
     */
    public void logout(UserPrincipal userPrincipal) {
        log.info("로그아웃 - 사용자: {}", userPrincipal.getEmail());
        // TODO: Redis에서 토큰 무효화 처리
    }

    // ========== Helper Methods ==========

    private UserPrincipal toUserPrincipal(User user) {
        return UserPrincipal.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                null,  // role은 Tenant 테이블에서 관리
                null   // tenantId는 Tenant 테이블에서 관리
        );
    }

    private TokenResponse buildTokenResponse(TokenPair tokenPair, User user) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .build();

        return TokenResponse.builder()
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .user(userResponse)
                .build();
    }
}
