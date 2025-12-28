// core-security 모듈
// prj-core의 @cocrepo/be-common (Guards, Strategies) 역할

plugins {
    `java-library`
}

dependencies {
    // 공통 모듈
    api(project(":modules:core-common"))
    api(project(":modules:core-vo"))

    // Spring Security
    api("org.springframework.boot:spring-boot-starter-security")

    // JWT
    api("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Servlet API
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}
