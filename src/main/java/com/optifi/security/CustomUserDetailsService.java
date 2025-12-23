package com.optifi.security;

import com.optifi.models.User;
import com.optifi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with this username: " + username));

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                getAuthorities(user),
                user.getId(),
                user.getRole()
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        switch (user.getRole()) {
            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
            case MODERATOR:
                authorities.add(new SimpleGrantedAuthority("ROLE_MODERATOR"));
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
            case USER:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
        }

        return authorities;
    }
}