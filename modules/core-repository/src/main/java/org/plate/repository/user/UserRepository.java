package org.plate.repository.user;

import org.plate.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository
 * prj-core의 UsersRepository와 동일
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 활성 사용자만 이메일로 조회 (삭제되지 않은 사용자)
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.removedAt IS NULL")
    Optional<User> findActiveByEmail(@Param("email") String email);

    /**
     * ID로 활성 사용자 조회 (삭제되지 않은 사용자)
     */
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.removedAt IS NULL")
    Optional<User> findActiveById(@Param("id") String id);
}
