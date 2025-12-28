package org.plate.security.config;

import lombok.Getter;
import lombok.Setter;
import org.plate.common.constant.AuthConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 관련 설정값을 담는 Configuration Properties
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명에 사용할 비밀키 (최소 256bit 권장)
     */
    private String secret;

    /**
     * Access Token 만료 시간 (밀리초)
     * 기본값: 15분 (900000ms)
     */
    private long accessTokenExpiration = AuthConstants.DEFAULT_ACCESS_TOKEN_EXPIRATION;

    /**
     * Refresh Token 만료 시간 (밀리초)
     * 기본값: 7일 (604800000ms)
     */
    private long refreshTokenExpiration = AuthConstants.DEFAULT_REFRESH_TOKEN_EXPIRATION;

    /**
     * JWT 발급자
     */
    private String issuer = "plate-server";
}
