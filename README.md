# Plate Spring Server

prj-core(NestJS)와 동일한 구조를 가진 Spring Boot 기반 서버 프로젝트입니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.4.1**
- **Spring Security** (JWT 기반 인증)
- **Spring Data JPA** (Hibernate 6.6)
- **PostgreSQL** (Supabase)
- **Gradle 8.11** (Kotlin DSL)
- **Springdoc OpenAPI** (Swagger UI)

## 프로젝트 구조

Gradle 멀티모듈 구조로 NestJS 모노레포의 packages 구조와 동일하게 구성되어 있습니다.

```
spring-server/
├── apps/
│   └── server/                    # Spring Boot 애플리케이션 (메인 진입점)
│       └── src/main/java/org/plate/server/
│           ├── controller/        # REST 컨트롤러
│           └── config/            # 설정 클래스
│
├── modules/
│   ├── core-common/               # 공통 상수, 열거형, 예외, 응답 형식
│   ├── core-vo/                   # Value Objects (PlainPassword, HashedPassword, TokenPair)
│   ├── core-dto/                  # Request/Response DTO
│   ├── core-entity/               # JPA 엔티티 (User, Tenant)
│   ├── core-repository/           # Spring Data JPA Repository
│   ├── core-security/             # JWT, 인증/인가 관련
│   └── core-service/              # 비즈니스 로직 (AuthFacade, UserService)
│
├── build.gradle.kts               # 루트 빌드 설정
├── settings.gradle.kts            # 모듈 설정
└── gradle/                        # Gradle Wrapper
```

## NestJS → Spring 용어 매핑

| NestJS | Spring |
|--------|--------|
| Module | @Configuration + 패키지 구조 |
| Controller | @RestController |
| Service | @Service |
| Guard | JwtAuthenticationFilter + SecurityConfig |
| Strategy | JwtProvider |
| Decorator | Annotation (@CurrentUser) |
| Pipe | @Valid + Bean Validation |
| DTO | Request/Response DTO |
| Entity (Prisma) | JPA Entity |

## 시작하기

### 사전 요구사항

- Java 17+
- Gradle 8.x (또는 Gradle Wrapper 사용)

### 환경 설정

1. `.env.example`을 복사하여 `.env` 파일을 생성합니다:

```bash
cp apps/server/.env.example apps/server/.env
```

2. `.env` 파일에서 필요한 환경 변수를 설정합니다:

```properties
# 데이터베이스 설정 (Supabase PostgreSQL)
DB_HOST=your-supabase-host
DB_PORT=5432
DB_NAME=postgres
DB_USERNAME=your-username
DB_PASSWORD=your-password

# JWT 설정 (최소 32바이트 이상)
JWT_SECRET=your-super-secret-jwt-key-that-is-at-least-32-bytes-long
```

### 실행

```bash
# 개발 서버 실행
./gradlew :apps:server:bootRun

# 빌드
./gradlew build

# JAR 파일 생성
./gradlew :apps:server:bootJar
```

### API 문서

서버 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## API 엔드포인트

### 인증 API (`/v1/auth`)

| 메서드 | 경로 | 설명 | 인증 필요 |
|--------|------|------|-----------|
| POST | `/v1/auth/sign-up` | 회원가입 | X |
| POST | `/v1/auth/login` | 로그인 | X |
| POST | `/v1/auth/token/refresh` | 토큰 갱신 | X |
| GET | `/v1/auth/verify-token` | 토큰 검증 | O |
| POST | `/v1/auth/logout` | 로그아웃 | O |

### 요청/응답 예시

**회원가입**
```bash
curl -X POST http://localhost:8080/v1/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "홍길동",
    "phone": "010-1234-5678"
  }'
```

**로그인**
```bash
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

## 프로파일

| 프로파일 | 설명 |
|----------|------|
| `local` | 로컬 개발 환경 (Supabase PostgreSQL) |
| `dev` | 개발 서버 환경 |
| `prod` | 프로덕션 환경 |

```bash
# 특정 프로파일로 실행
SPRING_PROFILES_ACTIVE=local ./gradlew :apps:server:bootRun
```

## 라이선스

Private
