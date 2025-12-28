package org.plate.repository.tenant;

import org.plate.entity.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 테넌트 Repository
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {

    /**
     * 사용자 ID로 테넌트 목록 조회
     */
    List<Tenant> findByUserId(String userId);

    /**
     * 사용자의 활성 테넌트 조회 (삭제되지 않은 테넌트)
     */
    @Query("SELECT t FROM Tenant t WHERE t.userId = :userId AND t.removedAt IS NULL")
    List<Tenant> findActiveByUserId(@Param("userId") String userId);

    /**
     * 사용자의 특정 테넌트 조회
     */
    Optional<Tenant> findByIdAndUserId(String id, String userId);

    /**
     * 사용자의 메인 테넌트 조회
     */
    @Query("SELECT t FROM Tenant t WHERE t.userId = :userId AND t.main = true AND t.removedAt IS NULL")
    Optional<Tenant> findMainTenantByUserId(@Param("userId") String userId);
}
