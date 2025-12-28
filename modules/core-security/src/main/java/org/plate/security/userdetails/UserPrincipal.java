package org.plate.security.userdetails;

import lombok.Builder;
import lombok.Getter;
import org.plate.common.constant.AuthConstants;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 인증된 사용자 정보
 * prj-core의 Request.user에 설정되는 User 정보와 동일한 역할
 */
@Getter
@Builder
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String email;
    private final String name;
    private final String role;
    private final String tenantId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return null; // JWT 기반 인증이므로 비밀번호 불필요
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * JWT 클레임에서 UserPrincipal 생성
     */
    public static UserPrincipal of(String id, String email, String name, String role, String tenantId) {
        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .name(name)
                .role(role != null ? role : AuthConstants.DEFAULT_ROLE)
                .tenantId(tenantId)
                .build();
    }
}
