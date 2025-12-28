// core-service 모듈
// prj-core의 @cocrepo/service, @cocrepo/facade 역할

plugins {
    `java-library`
}

dependencies {
    // 모듈 의존성
    api(project(":modules:core-dto"))
    api(project(":modules:core-entity"))
    api(project(":modules:core-repository"))
    api(project(":modules:core-security"))
    api(project(":modules:core-vo"))

    // Spring
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
}
