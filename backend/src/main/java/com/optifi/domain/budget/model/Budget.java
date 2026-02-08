package com.optifi.domain.budget.model;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.category.model.Category;
import com.optifi.domain.shared.BudgetPeriod;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "budgets",
        indexes = {
                @Index(name = "idx_budgets_user_id", columnList = "user_id"),
                @Index(name = "idx_budgets_user_active_range", columnList = "user_id,start_date,end_date"),
                @Index(name = "idx_budgets_accounts_account_id", columnList = "account_id"),
                @Index(name = "idx_budgets_categories_category_id", columnList = "category_id")

        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private BudgetPeriod period;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "budget_accounts",
            joinColumns = @JoinColumn(name = "budget_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    @Builder.Default
    private Set<Account> accounts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "budget_categories",
            joinColumns = @JoinColumn(name = "budget_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private boolean archived = false;

    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;


}
