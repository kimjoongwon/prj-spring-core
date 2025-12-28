// core-entity 모듈
// prj-core의 @cocrepo/entity 역할 (도메인 엔티티)

plugins {
    `java-library`
}

dependencies {
    // 공통 모듈
    api(project(":modules:core-common"))

    // JPA (Entity 어노테이션 + Auditing)
    api("org.springframework.boot:spring-boot-starter-data-jpa")
}
