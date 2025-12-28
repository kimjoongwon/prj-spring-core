package org.plate.vo.token;

/**
 * Access Token과 Refresh Token 쌍을 담는 Value Object
 */
public record TokenPair(
        String accessToken,
        String refreshToken
) {
    public static TokenPair of(String accessToken, String refreshToken) {
        return new TokenPair(accessToken, refreshToken);
    }
}
