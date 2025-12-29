# syntax=docker/dockerfile:1.4

# ============================================
# 빌드 스테이지 (네이티브 아키텍처 - 빠름)
# JAR 파일은 아키텍처 독립적이므로 arm64에서 빌드해도 됨
# ============================================
FROM --platform=$BUILDPLATFORM gradle:8.11-jdk17 AS builder

WORKDIR /app

COPY . .

# 네이티브 아키텍처로 빌드 (arm64 Mac에서 빠름)
RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    --mount=type=cache,target=/app/.gradle \
    gradle :apps:server:bootJar --no-daemon -x test

# ============================================
# 런타임 스테이지 (타겟 아키텍처 - amd64)
# ============================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 보안을 위한 non-root 사용자 생성 및 curl 설치
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -s /bin/bash appuser

# JAR 파일 복사 (아키텍처 독립적)
COPY --from=builder /app/apps/server/build/libs/plate-server.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
