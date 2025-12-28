package org.plate.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.plate.common.constant.AuthConstants;
import org.plate.common.enums.TokenType;
import org.plate.security.config.JwtProperties;
import org.plate.security.userdetails.UserPrincipal;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 토큰 생성 및 검증
 * prj-core의 TokenService와 JwtStrategy 역할을 수행
 */
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(UserPrincipal userPrincipal) {
        return generateToken(userPrincipal, jwtProperties.getAccessTokenExpiration(), TokenType.ACCESS);
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        return generateToken(userPrincipal, jwtProperties.getRefreshTokenExpiration(), TokenType.REFRESH);
    }

    /**
     * 토큰 생성 (공통)
     */
    private String generateToken(UserPrincipal userPrincipal, long expiration, TokenType tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put(AuthConstants.CLAIM_EMAIL, userPrincipal.getEmail());
        claims.put(AuthConstants.CLAIM_NAME, userPrincipal.getName());
        claims.put(AuthConstants.CLAIM_ROLE, userPrincipal.getRole());
        claims.put(AuthConstants.CLAIM_TENANT_ID, userPrincipal.getTenantId());
        claims.put(AuthConstants.CLAIM_TYPE, tokenType.name());

        return Jwts.builder()
                .subject(userPrincipal.getId())
                .claims(claims)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 토큰에서 UserPrincipal 추출
     */
    public UserPrincipal getUserPrincipalFromToken(String token) {
        Claims claims = parseToken(token);
        return UserPrincipal.of(
                claims.getSubject(),
                claims.get(AuthConstants.CLAIM_EMAIL, String.class),
                claims.get(AuthConstants.CLAIM_NAME, String.class),
                claims.get(AuthConstants.CLAIM_ROLE, String.class),
                claims.get(AuthConstants.CLAIM_TENANT_ID, String.class)
        );
    }

    /**
     * 토큰 타입 확인
     */
    public TokenType getTokenType(String token) {
        Claims claims = parseToken(token);
        String type = claims.get(AuthConstants.CLAIM_TYPE, String.class);
        return TokenType.valueOf(type);
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT 토큰이 만료되었습니다: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("지원되지 않는 JWT 토큰입니다: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("잘못된 형식의 JWT 토큰입니다: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.warn("JWT 서명 검증에 실패했습니다: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT 토큰이 비어있습니다: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * 토큰 파싱
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰 만료 시간 추출
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }
}
