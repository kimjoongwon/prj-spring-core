package org.plate.entity.tenant;

import jakarta.persistence.*;
import lombok.*;
import org.plate.entity.base.BaseEntity;

/**
 * 테넌트 엔티티
 * prj-core의 Prisma Tenant 모델과 동일한 구조 (멀티테넌트 지원)
 */
@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "space_id", nullable = false)
    private String spaceId;

    @Column(name = "role_id", nullable = false)
    private String roleId;

    @Column(name = "main", nullable = false)
    @Builder.Default
    private Boolean main = false;

    /**
     * 정적 팩토리 메서드
     */
    public static Tenant create(String userId, String spaceId, String roleId, boolean main) {
        return Tenant.builder()
                .userId(userId)
                .spaceId(spaceId)
                .roleId(roleId)
                .main(main)
                .build();
    }

    /**
     * 메인 테넌트 설정
     */
    public void setAsMain() {
        this.main = true;
    }
}
