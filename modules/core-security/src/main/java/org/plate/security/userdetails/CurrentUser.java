package org.plate.security.userdetails;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * 현재 인증된 사용자를 주입받기 위한 어노테이션
 * prj-core의 @CurrentUser 데코레이터와 동일한 역할
 *
 * 사용 예:
 * <pre>
 * {@code
 * @GetMapping("/me")
 * public ApiResponse<UserResponse> getMe(@CurrentUser UserPrincipal user) {
 *     return ApiResponse.success(userService.getUser(user.getId()));
 * }
 * }
 * </pre>
 */
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
