// core-vo 모듈
// prj-core의 @cocrepo/vo 역할 (Value Objects)

plugins {
    `java-library`
}

dependencies {
    // 공통 모듈
    api(project(":modules:core-common"))

    // 비밀번호 암호화
    implementation("org.springframework.security:spring-security-crypto")
}
