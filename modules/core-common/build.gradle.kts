// core-common 모듈
// prj-core의 @cocrepo/constant, @cocrepo/enum, @cocrepo/decorator 역할

plugins {
    `java-library`
}

dependencies {
    // Validation (어노테이션용)
    api("jakarta.validation:jakarta.validation-api")

    // Swagger/OpenAPI 어노테이션
    api("org.springdoc:springdoc-openapi-starter-common:2.7.0")

    // Spring Context (어노테이션용)
    compileOnly("org.springframework:spring-context")
    compileOnly("org.springframework:spring-web")
}
