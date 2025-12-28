package org.plate.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.plate.entity.base.BaseEntity;

/**
 * 사용자 엔티티
 * prj-core의 Prisma User 모델과 동일한 구조
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 정적 팩토리 메서드
     */
    public static User create(String email, String hashedPassword, String name, String phone) {
        return User.builder()
                .email(email)
                .password(hashedPassword)
                .name(name)
                .phone(phone)
                .build();
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }

    /**
     * 프로필 업데이트
     */
    public void updateProfile(String name) {
        this.name = name;
    }
}
