package org.plate.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 정의
 * prj-core의 AuthErrorMessages와 유사한 역할
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ========== 공통 에러 (COMMON_XXX) ==========
    COMMON_INVALID_REQUEST(400, "COMMON_001", "잘못된 요청입니다"),
    COMMON_INTERNAL_ERROR(500, "COMMON_002", "서버 내부 오류가 발생했습니다"),
    COMMON_NOT_FOUND(404, "COMMON_003", "요청한 리소스를 찾을 수 없습니다"),
    COMMON_METHOD_NOT_ALLOWED(405, "COMMON_004", "허용되지 않은 HTTP 메서드입니다"),

    // ========== 인증 에러 (AUTH_XXX) ==========
    AUTH_INVALID_CREDENTIALS(401, "AUTH_001", "이메일 또는 비밀번호가 올바르지 않습니다"),
    AUTH_TOKEN_EXPIRED(401, "AUTH_002", "인증 토큰이 만료되었습니다"),
    AUTH_TOKEN_INVALID(401, "AUTH_003", "유효하지 않은 인증 토큰입니다"),
    AUTH_TOKEN_MISSING(401, "AUTH_004", "인증 토큰이 필요합니다"),
    AUTH_REFRESH_TOKEN_EXPIRED(401, "AUTH_005", "리프레시 토큰이 만료되었습니다"),
    AUTH_REFRESH_TOKEN_INVALID(401, "AUTH_006", "유효하지 않은 리프레시 토큰입니다"),
    AUTH_UNAUTHORIZED(401, "AUTH_007", "인증이 필요합니다"),
    AUTH_ACCESS_DENIED(403, "AUTH_008", "접근 권한이 없습니다"),

    // ========== 사용자 에러 (USER_XXX) ==========
    USER_NOT_FOUND(404, "USER_001", "사용자를 찾을 수 없습니다"),
    USER_EMAIL_DUPLICATE(409, "USER_002", "이미 사용 중인 이메일입니다"),
    USER_ALREADY_EXISTS(409, "USER_003", "이미 존재하는 사용자입니다"),
    USER_INVALID_PASSWORD(400, "USER_004", "비밀번호 형식이 올바르지 않습니다"),

    // ========== 유효성 검사 에러 (VALIDATION_XXX) ==========
    VALIDATION_ERROR(400, "VALIDATION_001", "입력값 검증에 실패했습니다"),
    VALIDATION_EMAIL_FORMAT(400, "VALIDATION_002", "올바른 이메일 형식이 아닙니다"),
    VALIDATION_PASSWORD_LENGTH(400, "VALIDATION_003", "비밀번호는 8자 이상이어야 합니다");

    private final int status;
    private final String code;
    private final String message;
}
