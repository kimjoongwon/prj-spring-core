import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.4.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

// ========== 전체 프로젝트 공통 설정 ==========
allprojects {
    group = "org.plate"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

// ========== 서브프로젝트 공통 설정 ==========
subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    // Spring Boot BOM 적용 (버전 관리)
    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.1")
        }
    }

    dependencies {
        // Lombok (모든 모듈에서 사용)
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // 테스트
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// ========== 모듈별 의존성 정의 ==========
// 편의를 위한 의존성 그룹 정의

extra["springWebDeps"] = listOf(
    "org.springframework.boot:spring-boot-starter-web",
    "org.springframework.boot:spring-boot-starter-validation"
)

extra["springSecurityDeps"] = listOf(
    "org.springframework.boot:spring-boot-starter-security"
)

extra["springDataDeps"] = listOf(
    "org.springframework.boot:spring-boot-starter-data-jpa",
    "org.springframework.boot:spring-boot-starter-data-redis"
)

extra["jwtDeps"] = listOf(
    "io.jsonwebtoken:jjwt-api:0.12.6"
)

extra["jwtRuntimeDeps"] = listOf(
    "io.jsonwebtoken:jjwt-impl:0.12.6",
    "io.jsonwebtoken:jjwt-jackson:0.12.6"
)

extra["swaggerDeps"] = listOf(
    "org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0"
)
