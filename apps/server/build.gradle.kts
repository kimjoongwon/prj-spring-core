// apps/server 모듈
// prj-core의 apps/server 역할 (Spring Boot 애플리케이션)

plugins {
    id("org.springframework.boot")
}

dependencies {
    // 모든 모듈 의존성
    implementation(project(":modules:core-common"))
    implementation(project(":modules:core-vo"))
    implementation(project(":modules:core-dto"))
    implementation(project(":modules:core-entity"))
    implementation(project(":modules:core-repository"))
    implementation(project(":modules:core-security"))
    implementation(project(":modules:core-service"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // .env 파일 로드
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // OpenAPI (Swagger)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // 개발 도구
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // H2 Database (개발용)
    runtimeOnly("com.h2database:h2")

    // PostgreSQL (프로덕션용)
    runtimeOnly("org.postgresql:postgresql")

    // 테스트
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("plate-server.jar")
}
