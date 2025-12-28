// core-repository 모듈
// prj-core의 @cocrepo/repository 역할

plugins {
    `java-library`
}

dependencies {
    // Entity 모듈
    api(project(":modules:core-entity"))

    // Spring Data JPA
    api("org.springframework.boot:spring-boot-starter-data-jpa")
}
