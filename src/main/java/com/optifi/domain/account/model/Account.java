package com.optifi.domain.account.model;

import com.optifi.domain.shared.model.Currency;
import com.optifi.domain.transaction.model.Transaction;
import com.optifi.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "accounts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_account_name",
                columnNames = {"user_id", "name"}
        ),
        indexes = {
                @Index(name = "idx_accounts_user_id", columnList = "user_id"),
                @Index(name = "idx_accounts_archived", columnList = "archived")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Transaction> transactions = new HashSet<>();

    @Column(name = "institution", length = 100)
    private String institution;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Builder.Default
    @Column(name = "archived", nullable = false)
    private boolean archived = false;
}
