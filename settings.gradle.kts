rootProject.name = "spring-server"

// ========== 공유 모듈 (prj-core의 packages/ 대응) ==========
include(":modules:core-common")      // 상수, enum, 유틸리티, 어노테이션
include(":modules:core-vo")          // Value Objects
include(":modules:core-dto")         // Data Transfer Objects
include(":modules:core-entity")      // 도메인 엔티티
include(":modules:core-repository")  // Repository 레이어
include(":modules:core-security")    // JWT, 인증/인가
include(":modules:core-service")     // Service/Facade 레이어

// ========== 애플리케이션 (prj-core의 apps/ 대응) ==========
include(":apps:server")              // Spring Boot 애플리케이션
