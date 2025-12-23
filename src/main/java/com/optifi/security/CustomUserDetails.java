package com.optifi.security;

import com.optifi.models.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User {
    private Long id;
    private Role role;

    public CustomUserDetails(String username,
                             String password,
                             boolean enabled,
                             boolean accountNonExpired,
                             boolean credentialsNonExpired,
                             boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities,
                             long userId,
                             Role role) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = userId;
        this.role = role;
    }
}

