package org.plate.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.plate.common.constant.AuthConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 설정
 * prj-core의 Swagger 설정과 동일한 구조로 구현
 */
@Configuration
public class SwaggerConfig {

    @Value("${app.name:Plate Server}")
    private String appName;

    private static final String BEARER_TOKEN_PREFIX = "Bearer";
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("/").description("API 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, bearerSecurityScheme())
                        .addSecuritySchemes(AuthConstants.ACCESS_TOKEN_COOKIE, cookieSecurityScheme())
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME)
                        .addList(AuthConstants.ACCESS_TOKEN_COOKIE)
                );
    }

    private Info apiInfo() {
        return new Info()
                .title(appName)
                .version("1.0.0")
                .description("""
                        API 문서입니다. 대부분의 엔드포인트는 JWT 인증이 필요합니다.

                        ## 인증 방식
                        - **Cookie 기반**: accessToken 쿠키로 자동 전송
                        - **Bearer Token**: Authorization 헤더에 Bearer 토큰 전송

                        ## 응답 형식
                        모든 응답은 다음 형식을 따릅니다:
                        ```json
                        {
                          "success": true,
                          "message": "요청이 성공적으로 처리되었습니다",
                          "data": { ... }
                        }
                        ```
                        """)
                .contact(new Contact()
                        .name("Plate Team")
                        .email("support@plate.org")
                );
    }

    private SecurityScheme bearerSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER_TOKEN_PREFIX.toLowerCase())
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT Access Token을 입력하세요 (Bearer 접두사 불필요)");
    }

    private SecurityScheme cookieSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name(AuthConstants.ACCESS_TOKEN_COOKIE)
                .description("JWT Access Token (HttpOnly 쿠키로 자동 전송)");
    }
}
