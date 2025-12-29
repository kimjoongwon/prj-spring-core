#!/bin/bash

# Harbor 배포 스크립트
# 사용법: ./deploy.sh [stg|prod] [태그]

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Harbor 설정
HARBOR_REGISTRY="harbor.cocdev.co.kr"
IMAGE_NAME="spring-server"

# 환경 선택
select_env() {
    if [ -n "$1" ]; then
        ENV=$1
    else
        echo -e "${YELLOW}배포 환경을 선택하세요:${NC}"
        echo "1) stg  - 스테이징 환경"
        echo "2) prod - 프로덕션 환경"
        read -p "선택 (1 또는 2): " choice
        case $choice in
            1) ENV="stg" ;;
            2) ENV="prod" ;;
            *) echo -e "${RED}잘못된 선택입니다.${NC}"; exit 1 ;;
        esac
    fi

    # 환경 검증
    if [[ "$ENV" != "stg" && "$ENV" != "prod" ]]; then
        echo -e "${RED}잘못된 환경입니다. stg 또는 prod를 선택하세요.${NC}"
        exit 1
    fi
}

# 태그 설정
set_tag() {
    if [ -n "$1" ]; then
        TAG=$1
    else
        # 기본 태그: 현재 날짜 + 시간
        TAG=$(date +%Y%m%d-%H%M%S)
        read -p "이미지 태그를 입력하세요 (기본값: $TAG): " input_tag
        if [ -n "$input_tag" ]; then
            TAG=$input_tag
        fi
    fi
}

# 이미지 전체 경로
get_full_image_path() {
    echo "${HARBOR_REGISTRY}/${ENV}/${IMAGE_NAME}:${TAG}"
}

# Harbor 로그인
harbor_login() {
    echo -e "${YELLOW}Harbor 레지스트리에 로그인합니다...${NC}"
    podman login ${HARBOR_REGISTRY}
    if [ $? -ne 0 ]; then
        echo -e "${RED}Harbor 로그인 실패${NC}"
        exit 1
    fi
    echo -e "${GREEN}Harbor 로그인 성공${NC}"
}

# 이미지 빌드
build_image() {
    local full_path=$(get_full_image_path)
    echo -e "${YELLOW}이미지 빌드 중: ${full_path}${NC}"
    # --layers: 레이어 캐싱 활성화
    # Dockerfile의 --mount=type=cache로 Gradle 캐시 재사용
    podman build \
        --platform linux/amd64 \
        --layers \
        -t ${full_path} .
    if [ $? -ne 0 ]; then
        echo -e "${RED}이미지 빌드 실패${NC}"
        exit 1
    fi
    echo -e "${GREEN}이미지 빌드 완료${NC}"
}

# 이미지 푸시
push_image() {
    local full_path=$(get_full_image_path)
    echo -e "${YELLOW}이미지 푸시 중: ${full_path}${NC}"
    podman push ${full_path}
    if [ $? -ne 0 ]; then
        echo -e "${RED}이미지 푸시 실패${NC}"
        exit 1
    fi
    echo -e "${GREEN}이미지 푸시 완료${NC}"
}

# latest 태그 추가 및 푸시
push_latest() {
    local full_path=$(get_full_image_path)
    local latest_path="${HARBOR_REGISTRY}/${ENV}/${IMAGE_NAME}:latest"

    echo -e "${YELLOW}latest 태그 추가 중...${NC}"
    podman tag ${full_path} ${latest_path}
    podman push ${latest_path}
    echo -e "${GREEN}latest 태그 푸시 완료${NC}"
}

# 메인 실행
main() {
    echo "========================================"
    echo "     Harbor 배포 스크립트"
    echo "========================================"

    # 환경 및 태그 설정
    select_env $1
    set_tag $2

    local full_path=$(get_full_image_path)

    echo ""
    echo -e "${GREEN}배포 정보:${NC}"
    echo "  - 환경: ${ENV}"
    echo "  - 이미지: ${full_path}"
    echo ""

    read -p "진행하시겠습니까? (y/n): " confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        echo "배포를 취소합니다."
        exit 0
    fi

    # 배포 실행
    harbor_login
    build_image
    push_image
    push_latest

    echo ""
    echo "========================================"
    echo -e "${GREEN}배포 완료!${NC}"
    echo "  - 이미지: ${full_path}"
    echo "  - Latest: ${HARBOR_REGISTRY}/${ENV}/${IMAGE_NAME}:latest"
    echo "========================================"
}

main "$@"
