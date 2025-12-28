package org.plate.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.plate.common.constant.AuthConstants;
import org.plate.common.enums.TokenType;
import org.plate.security.userdetails.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * JWT 인증 필터
 * prj-core의 JwtStrategy + JwtAuthGuard 역할을 수행
 *
 * 토큰 추출 우선순위:
 * 1. Cookie의 accessToken
 * 2. Authorization 헤더의 Bearer 토큰
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
                // Access Token인지 확인
                if (jwtProvider.getTokenType(token) == TokenType.ACCESS) {
                    UserPrincipal userPrincipal = jwtProvider.getUserPrincipalFromToken(token);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal,
                                    null,
                                    userPrincipal.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("인증 성공 - 사용자: {}", userPrincipal.getEmail());
                }
            }
        } catch (Exception ex) {
            log.error("SecurityContext에 사용자 인증 정보를 설정할 수 없습니다: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청에서 JWT 토큰 추출
     * prj-core의 JwtStrategy.extractToken과 동일한 로직
     */
    private String extractToken(HttpServletRequest request) {
        // 1. 쿠키에서 추출 (우선순위 높음)
        String tokenFromCookie = extractTokenFromCookie(request);
        if (StringUtils.hasText(tokenFromCookie)) {
            return tokenFromCookie;
        }

        // 2. Authorization 헤더에서 추출 (fallback)
        return extractTokenFromHeader(request);
    }

    /**
     * 쿠키에서 토큰 추출
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> AuthConstants.ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AuthConstants.BEARER_PREFIX)) {
            return bearerToken.substring(AuthConstants.BEARER_PREFIX.length());
        }
        return null;
    }
}
