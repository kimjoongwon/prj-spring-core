package org.plate.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 설정
 * context-path를 Swagger에서 인식하도록 서버 URL 설정
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(ServletContext servletContext) {
        String contextPath = servletContext.getContextPath();

        Server server = new Server()
                .url(contextPath)
                .description("현재 서버");

        return new OpenAPI()
                .servers(List.of(server));
    }
}
