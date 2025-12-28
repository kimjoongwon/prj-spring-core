package org.plate.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.plate.common.enums.ErrorCode;

/**
 * API 응답 래퍼
 * prj-core의 ResponseEntityInterceptor 역할을 수행
 *
 * @param <T> 응답 데이터 타입
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API 공통 응답 형식")
public class ApiResponse<T> {

    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T data;

    @Schema(description = "에러 코드 (에러 발생 시)", example = "AUTH_001")
    private final String errorCode;

    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다")
                .data(data)
                .build();
    }

    /**
     * 성공 응답 생성 (데이터 + 메시지)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 성공 응답 생성 (메시지만)
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * 실패 응답 생성 (에러 코드 포함)
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    /**
     * 실패 응답 생성 (ErrorCode enum 사용)
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(errorCode.getMessage())
                .errorCode(errorCode.getCode())
                .build();
    }
}
