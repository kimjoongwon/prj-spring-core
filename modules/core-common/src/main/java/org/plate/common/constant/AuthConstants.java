package org.plate.common.constant;

/**
 * 인증 관련 상수
 * prj-core의 @cocrepo/constant 역할
 */
public final class AuthConstants {

    private AuthConstants() {
        // 인스턴스화 방지
    }

    // ========== 토큰 관련 ==========
    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // ========== 클레임 키 ==========
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_NAME = "name";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TENANT_ID = "tenantId";
    public static final String CLAIM_TYPE = "type";

    // ========== 기본값 ==========
    public static final String DEFAULT_ROLE = "USER";
    public static final long DEFAULT_ACCESS_TOKEN_EXPIRATION = 900000L;      // 15분
    public static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = 604800000L;  // 7일
}
