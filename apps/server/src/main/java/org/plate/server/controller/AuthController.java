package org.plate.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.plate.common.constant.AuthConstants;
import org.plate.common.enums.ErrorCode;
import org.plate.common.exception.BusinessException;
import org.plate.common.response.ApiResponse;
import org.plate.dto.auth.request.LoginRequest;
import org.plate.dto.auth.request.RefreshTokenRequest;
import org.plate.dto.auth.request.SignUpRequest;
import org.plate.dto.auth.response.TokenResponse;
import org.plate.dto.auth.response.UserResponse;
import org.plate.security.jwt.TokenService;
import org.plate.security.userdetails.CurrentUser;
import org.plate.security.userdetails.UserPrincipal;
import org.plate.service.auth.AuthFacade;
import org.plate.vo.token.TokenPair;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 인증 컨트롤러
 * prj-core의 AuthController와 동일한 구조
 *
 * 엔드포인트:
 * - POST /v1/auth/login         : 로그인
 * - POST /v1/auth/sign-up       : 회원가입
 * - POST /v1/auth/token/refresh : 토큰 갱신
 * - GET  /v1/auth/verify-token  : 토큰 검증
 * - POST /v1/auth/logout        : 로그아웃
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인, 회원가입, 토큰 관리 API")
public class AuthController {

    private final AuthFacade authFacade;
    private final TokenService tokenService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다. 성공 시 Access Token과 Refresh Token이 쿠키에 설정됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (이메일 또는 비밀번호 불일치)"
            )
    })
    public ApiResponse<TokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = authFacade.login(request);

        // 쿠키 설정
        tokenService.setTokenCookies(
                response,
                TokenPair.of(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken())
        );

        return ApiResponse.success(tokenResponse, "로그인 성공");
    }

    /**
     * 회원가입
     */
    @PostMapping("/sign-up")
    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다. 성공 시 자동으로 로그인됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이메일 중복"
            )
    })
    public ApiResponse<TokenResponse> signUp(
            @Valid @RequestBody SignUpRequest request,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = authFacade.signUp(request);

        // 쿠키 설정
        tokenService.setTokenCookies(
                response,
                TokenPair.of(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken())
        );

        return ApiResponse.success(tokenResponse, "회원가입 성공");
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/token/refresh")
    @Operation(
            summary = "토큰 갱신",
            description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다. " +
                    "쿠키의 refreshToken을 우선 사용하며, 없는 경우 요청 바디의 refreshToken을 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않거나 만료된 Refresh Token"
            )
    })
    public ApiResponse<TokenResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response
    ) {
        // 1. 쿠키에서 Refresh Token 추출 (우선순위 높음)
        String refreshToken = extractRefreshTokenFromCookie(httpRequest);

        // 2. 쿠키에 없으면 요청 바디에서 추출
        if (!StringUtils.hasText(refreshToken) && request != null) {
            refreshToken = request.getRefreshToken();
        }

        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
        }

        TokenResponse tokenResponse = authFacade.refreshToken(refreshToken);

        // 새 토큰을 쿠키에 설정
        tokenService.setTokenCookies(
                response,
                TokenPair.of(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken())
        );

        return ApiResponse.success(tokenResponse, "토큰 갱신 성공");
    }

    /**
     * 토큰 검증
     */
    @GetMapping("/verify-token")
    @Operation(
            summary = "토큰 검증",
            description = "현재 Access Token의 유효성을 검증하고 사용자 정보를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 유효",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 토큰"
            )
    })
    public ApiResponse<UserResponse> verifyToken(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        UserResponse userResponse = authFacade.verifyToken(userPrincipal);
        return ApiResponse.success(userResponse, "토큰이 유효합니다");
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "현재 사용자를 로그아웃합니다. 토큰 쿠키가 삭제됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공"
            )
    })
    public ApiResponse<Boolean> logout(
            @CurrentUser UserPrincipal userPrincipal,
            HttpServletResponse response
    ) {
        authFacade.logout(userPrincipal);
        tokenService.clearTokenCookies(response);

        return ApiResponse.success(true, "로그아웃 성공");
    }

    /**
     * 쿠키에서 Refresh Token 추출
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> AuthConstants.REFRESH_TOKEN_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
