package org.plate.vo.password;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 해시된 비밀번호 Value Object
 * prj-core의 HashedPassword와 동일
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HashedPassword {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final String value;

    /**
     * 평문 비밀번호로부터 해시 생성
     */
    public static HashedPassword fromPlain(PlainPassword plainPassword) {
        String hashed = PASSWORD_ENCODER.encode(plainPassword.getValue());
        return new HashedPassword(hashed);
    }

    /**
     * 저장된 해시값으로부터 생성 (DB에서 로드 시)
     */
    public static HashedPassword fromHash(String hashedValue) {
        return new HashedPassword(hashedValue);
    }

    /**
     * 평문 비밀번호와 비교
     *
     * @param plainPassword 비교할 평문 비밀번호
     * @return 일치 여부
     */
    public boolean matches(PlainPassword plainPassword) {
        return PASSWORD_ENCODER.matches(plainPassword.getValue(), this.value);
    }

    /**
     * 문자열과 직접 비교 (편의 메서드)
     */
    public boolean matches(String rawPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, this.value);
    }

    @Override
    public String toString() {
        return "[HASHED]";
    }
}
