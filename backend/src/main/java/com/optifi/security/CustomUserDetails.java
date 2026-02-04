package com.optifi.security;

import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class CustomUserDetails extends User {
    private Long id;
    private Role role;
    private ZoneId zoneId;
    private Currency currency;

    public CustomUserDetails(String username,
                             String password,
                             boolean enabled,
                             boolean accountNonExpired,
                             boolean credentialsNonExpired,
                             boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities,
                             long userId,
                             Role role,
                             Currency currency) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = userId;
        this.role = role;
        this.currency = currency;
        this.zoneId = ZoneId.of("Europe/Sofia"); //TODO user timezone, add token refresh on preference change
    }


    public static CustomUserDetails fromUser(com.optifi.domain.user.model.User user) {
        String password = user.getPasswordHash();
        if (password == null) password = ""; // Google users: no local password

        return new CustomUserDetails(
                user.getUsername(),
                password,
                true,
                true,
                true,
                user.getRole() != Role.BLOCKED,
                authoritiesFor(user.getRole()),
                user.getId(),
                user.getRole(),
                user.getBaseCurrency()
        );
    }

    private static Collection<? extends GrantedAuthority> authoritiesFor(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        switch (role) {
            case ADMIN -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            case MODERATOR -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            case USER -> authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }
}

