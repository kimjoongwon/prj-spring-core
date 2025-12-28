package org.plate.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.plate.common.constant.AuthConstants;
import org.plate.security.config.JwtProperties;
import org.plate.security.userdetails.UserPrincipal;
import org.plate.vo.token.TokenPair;

/**
 * 토큰 관리 서비스
 * prj-core의 TokenService와 동일한 역할
 */
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    /**
     * 토큰 쌍 생성
     */
    public TokenPair generateTokenPair(UserPrincipal userPrincipal) {
        String accessToken = jwtProvider.generateAccessToken(userPrincipal);
        String refreshToken = jwtProvider.generateRefreshToken(userPrincipal);

        return TokenPair.of(accessToken, refreshToken);
    }

    /**
     * Access Token 쿠키 설정
     */
    public void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = createCookie(
                AuthConstants.ACCESS_TOKEN_COOKIE,
                accessToken,
                (int) (jwtProperties.getAccessTokenExpiration() / 1000)
        );
        response.addCookie(cookie);
    }

    /**
     * Refresh Token 쿠키 설정
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = createCookie(
                AuthConstants.REFRESH_TOKEN_COOKIE,
                refreshToken,
                (int) (jwtProperties.getRefreshTokenExpiration() / 1000)
        );
        response.addCookie(cookie);
    }

    /**
     * 모든 토큰 쿠키 설정
     */
    public void setTokenCookies(HttpServletResponse response, TokenPair tokenPair) {
        setAccessTokenCookie(response, tokenPair.accessToken());
        setRefreshTokenCookie(response, tokenPair.refreshToken());
    }

    /**
     * 토큰 쿠키 삭제
     */
    public void clearTokenCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = createCookie(AuthConstants.ACCESS_TOKEN_COOKIE, "", 0);
        Cookie refreshTokenCookie = createCookie(AuthConstants.REFRESH_TOKEN_COOKIE, "", 0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    /**
     * 쿠키 생성 헬퍼
     */
    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
