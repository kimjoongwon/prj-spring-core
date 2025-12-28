package org.plate.vo.password;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 평문 비밀번호 Value Object
 * prj-core의 PlainPassword와 동일
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlainPassword {

    private final String value;

    /**
     * 평문 비밀번호 생성
     *
     * @param value 비밀번호 문자열
     * @return PlainPassword 인스턴스
     * @throws IllegalArgumentException 비밀번호가 유효하지 않은 경우
     */
    public static PlainPassword of(String value) {
        validate(value);
        return new PlainPassword(value);
    }

    /**
     * 비밀번호 유효성 검사
     */
    private static void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다");
        }
        if (value.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다");
        }
    }

    /**
     * 해시된 비밀번호로 변환
     */
    public HashedPassword toHashed() {
        return HashedPassword.fromPlain(this);
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}
