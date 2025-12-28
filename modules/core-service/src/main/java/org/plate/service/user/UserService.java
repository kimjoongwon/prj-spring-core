package org.plate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.plate.common.enums.ErrorCode;
import org.plate.common.exception.BusinessException;
import org.plate.entity.user.User;
import org.plate.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 사용자 서비스
 * prj-core의 UsersService와 동일
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * ID로 사용자 조회
     */
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * ID로 활성 사용자 조회
     */
    public User findActiveById(String id) {
        return userRepository.findActiveById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일로 사용자 조회
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 이메일로 활성 사용자 조회
     */
    public User findActiveByEmail(String email) {
        return userRepository.findActiveByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일 중복 확인
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 사용자 저장
     */
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 사용자 삭제 (소프트 삭제)
     */
    @Transactional
    public void delete(String id) {
        User user = findById(id);
        user.softDelete();
        userRepository.save(user);
    }
}
