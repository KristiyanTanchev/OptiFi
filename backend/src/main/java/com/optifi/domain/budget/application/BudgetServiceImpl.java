package com.optifi.domain.budget.application;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.budget.application.command.BudgetCreateCommand;
import com.optifi.domain.budget.application.command.BudgetQuery;
import com.optifi.domain.budget.application.command.BudgetSpecs;
import com.optifi.domain.budget.application.command.BudgetUpdateCommand;
import com.optifi.domain.budget.application.result.BudgetDetailsResult;
import com.optifi.domain.budget.model.Budget;
import com.optifi.domain.budget.repository.BudgetRepository;
import com.optifi.domain.category.model.Category;
import com.optifi.domain.category.repository.CategoryRepository;
import com.optifi.domain.shared.BudgetPeriod;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BudgetDetailsResult> getBudgetsForUser(BudgetQuery query, Pageable pageable) {
        Specification<Budget> spec = BudgetSpecs.fromQuery(query);
        Page<Budget> budgets = budgetRepository.findAll(spec, pageable);
        return budgets.map(BudgetDetailsResult::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetDetailsResult getBudgetById(long budgetId, long userId) {
        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget", budgetId));
        return BudgetDetailsResult.fromEntity(budget);
    }

    @Override
    public BudgetDetailsResult createBudget(BudgetCreateCommand cmd) {
        User user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", cmd.userId()));

        validateBudgetNameUniqueness(cmd.name(), cmd.userId());
        validateCorrectDates(cmd.startDate(), cmd.endDate(), cmd.budgetPeriod());

        List<Account> accounts = resolveAccounts(cmd.accountIds(), cmd.userId());

        List<Category> categories = resolveCategories(cmd.categoryIds(), cmd.userId());

        Budget budget = Budget.builder()
                .user(user)
                .name(cmd.name())
                .period(cmd.budgetPeriod())
                .amount(cmd.amount())
                .currency(cmd.currency())
                .startDate(cmd.startDate())
                .endDate(cmd.endDate())
                .accounts(new HashSet<>(accounts))
                .categories(new HashSet<>(categories))
                .build();
        return BudgetDetailsResult.fromEntity(budgetRepository.save(budget));
    }

    @Override
    public void updateBudget(BudgetUpdateCommand cmd) {
        Budget budget = budgetRepository.findByUser_IdAndId(cmd.userId(), cmd.budgetId())
                .orElseThrow(() -> new EntityNotFoundException("Budget", cmd.budgetId()));

        validateBudgetNameUniqueness(cmd.name(), cmd.userId(), cmd.budgetId());
        validateCorrectDates(cmd.startDate(), cmd.endDate(), cmd.budgetPeriod());

        budget.setName(cmd.name());
        budget.setPeriod(cmd.budgetPeriod());
        budget.setStartDate(cmd.startDate());
        budget.setEndDate(cmd.endDate());
        budget.setAmount(cmd.amount());
        budget.setCurrency(cmd.currency());
        budgetRepository.save(budget);
    }

    @Override
    public void archiveBudget(long budgetId, long userId) {
        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget", budgetId));
        if (budget.isArchived()) {
            throw new IllegalStateTransitionException("Budget is already archived");
        }
        budget.setArchived(true);
        budgetRepository.save(budget);
    }

    @Override
    public void unarchiveBudget(long budgetId, long userId) {
        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget", budgetId));
        if (!budget.isArchived()) {
            throw new IllegalStateTransitionException("Budget is not archived");
        }
        budget.setArchived(false);
        budgetRepository.save(budget);
    }

    @Override
    public void deleteBudget(long budgetId, long userId) {
        Budget budget = budgetRepository.findByUser_IdAndId(userId, budgetId)
                .orElseThrow(() -> new EntityNotFoundException("Budget", budgetId));
        budgetRepository.delete(budget);
    }

    private void validateBudgetNameUniqueness(String name, long userId) {
        if (budgetRepository.existsByUser_IdAndName(userId, name)) {
            throw new DuplicateEntityException("Budget", "name", name);
        }
    }

    private void validateBudgetNameUniqueness(String name, long userId, long excludedBudgetId) {
        if (budgetRepository.existsByUser_IdAndNameAndIdNot(userId, name, excludedBudgetId)) {
            throw new DuplicateEntityException("Budget", "name", name);
        }
    }

    private List<Account> resolveAccounts(List<Long> accountIds, long userId) {
        if (accountIds == null || accountIds.isEmpty()) {
            return accountRepository.findAllByUserId(userId);
        }
        List<Account> accounts = accountRepository.findAllById(accountIds);
        for (Account account : accounts) {
            if (!Objects.equals(account.getUser().getId(), userId)) {
                throw new EntityNotFoundException("Account", account.getId());
            }
        }
        if (accounts.size() != accountIds.size()) {
            throw new EntityNotFoundException("One or more accounts were not found");
        }
        return accounts;
    }

    private List<Category> resolveCategories(List<Long> categoryIds, long userId) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return categoryRepository.findAllByUserId(userId);
        }
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        for (Category category : categories) {
            if (!category.isDefault() && !Objects.equals(category.getUser().getId(), userId)) {
                throw new EntityNotFoundException("Category", category.getId());
            }
        }
        if (categories.size() != categoryIds.size()) {
            throw new EntityNotFoundException("One or more categories were not found");
        }
        return categories;
    }

    private void validateCorrectDates(LocalDate startDate, LocalDate endDate, BudgetPeriod period) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateException("Start date must be before end date");
        }
        switch (period) {
            case WEEK -> {
                if (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
                    throw new InvalidDateException("Start date must be Monday");
                }
                if (endDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    throw new InvalidDateException("End date must be Sunday");
                }
                if (!startDate.plusDays(6).equals(endDate)) {
                    throw new InvalidDateException("Start and end dates must be on the same week");
                }
            }
            case MONTH -> {
                if (startDate.getDayOfMonth() != 1) {
                    throw new InvalidDateException("Start date must be first day of the month");
                }
                if (endDate.getDayOfMonth() != endDate.lengthOfMonth()) {
                    throw new InvalidDateException("End date must be last day of the month");
                }
                if (!YearMonth.from(startDate).equals(YearMonth.from(endDate))) {
                    throw new InvalidDateException("Start and end dates must be on the same month");
                }
            }
            case YEAR -> {
                if (startDate.getDayOfYear() != 1) {
                    throw new InvalidDateException("Start date must be first day of the year");
                }
                if (!startDate.plusYears(1).minusDays(1).equals(endDate)) {
                    throw new InvalidDateException("Start and end dates must be on the same year");
                }
            }
        }
    }
}
