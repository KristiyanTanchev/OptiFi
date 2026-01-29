package com.optifi.domain.user.repository;

import com.optifi.domain.user.model.Role;
import com.optifi.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    <T> Optional<T> findByAuthProviderAndProviderSubject(String google, String sub);
}
