package org.plate.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Boot 애플리케이션 진입점
 * prj-core의 apps/server/main.ts와 동일한 역할
 */
@SpringBootApplication(scanBasePackages = "org.plate")
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "org.plate.repository")
@EntityScan(basePackages = "org.plate.entity")
@ConfigurationPropertiesScan(basePackages = "org.plate")
public class PlateServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlateServerApplication.class, args);
    }
}
