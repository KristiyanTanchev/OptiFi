package com.optifi.domain.user.model;


import com.optifi.domain.account.model.Account;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.Role;
import com.optifi.domain.shared.SupportedLocale;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_username", columnList = "username")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = {"email"}),
                @UniqueConstraint(name = "uk_users_username", columnNames = {"username"}),
                @UniqueConstraint(name = "uk_users_provider_subject", columnNames = {"auth_provider", "provider_subject"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false, length = 32)
    @Size(min = 3, max = 32)
    @NotBlank
    private String username;

    @Column(nullable = true)
    private String passwordHash;

    @Column(nullable = false, name = "auth_provider")
    private String authProvider;

    @Column(nullable = true, name = "provider_subject")
    private String providerSubject;


    @Column(nullable = false, length = 100, unique = true)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Size(min = 3, max = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private Set<Account> accounts = new HashSet<>();

    @Column(name = "base_currency", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Currency baseCurrency = Currency.USD;

    @Enumerated(EnumType.STRING)
    @Column(name = "locale", nullable = false, length = 16)
    @Builder.Default
    private SupportedLocale locale = SupportedLocale.EN_US;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void normalizeFields() {
        username = username.toLowerCase(Locale.ROOT);
        email = email.toLowerCase(Locale.ROOT);
    }

    public Set<Account> getAccounts() {
        return new HashSet<>(accounts);
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isModeratorOrAdmin() {
        return role == Role.ADMIN || role == Role.MODERATOR;
    }
}
