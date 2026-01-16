package com.optifi.domain.user.model;


import com.optifi.domain.account.model.Account;
import com.optifi.domain.shared.model.Currency;
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

    @Column(nullable = false)
    private String passwordHash;

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

    public void addAccount(Account account) {
        accounts.add(account);
        account.setUser(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setUser(null);
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
