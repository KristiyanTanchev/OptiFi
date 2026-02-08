package com.optifi.domain.budget.application;

import com.optifi.domain.budget.application.command.BudgetCreateCommand;
import com.optifi.domain.budget.application.command.BudgetQuery;
import com.optifi.domain.budget.application.command.BudgetUpdateCommand;
import com.optifi.domain.budget.application.result.BudgetDetailsResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BudgetService {
    Page<BudgetDetailsResult> getBudgetsForUser(BudgetQuery query, Pageable pageable);

    BudgetDetailsResult getBudgetById(long budgetId, long userId);

    BudgetDetailsResult createBudget(BudgetCreateCommand cmd);

    void updateBudget(BudgetUpdateCommand cmd);

    void archiveBudget(long budgetId, long userId);

    void unarchiveBudget(long budgetId, long userId);

    void deleteBudget(long budgetId, long userId);
}
