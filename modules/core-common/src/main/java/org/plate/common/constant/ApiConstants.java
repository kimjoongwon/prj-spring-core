package org.plate.common.constant;

/**
 * API 관련 상수
 */
public final class ApiConstants {

    private ApiConstants() {
    }

    // ========== API 버전 ==========
    public static final String API_V1 = "/v1";

    // ========== 기본 메시지 ==========
    public static final String DEFAULT_SUCCESS_MESSAGE = "요청이 성공적으로 처리되었습니다";
    public static final String DEFAULT_ERROR_MESSAGE = "요청 처리 중 오류가 발생했습니다";

    // ========== 페이지네이션 ==========
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}
