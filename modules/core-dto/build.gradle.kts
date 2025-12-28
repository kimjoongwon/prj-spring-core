// core-dto 모듈
// prj-core의 @cocrepo/dto 역할 (Data Transfer Objects)

plugins {
    `java-library`
}

dependencies {
    // 공통 모듈
    api(project(":modules:core-common"))

    // Validation
    api("jakarta.validation:jakarta.validation-api")

    // Swagger/OpenAPI 어노테이션 (core-common에서 전이됨)
}
