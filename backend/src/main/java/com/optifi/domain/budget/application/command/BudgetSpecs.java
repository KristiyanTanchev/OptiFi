package com.optifi.domain.budget.application.command;

import com.optifi.domain.budget.model.Budget;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class BudgetSpecs {

    public static Specification<Budget> fromQuery(BudgetQuery query) {
        return Specification.allOf(
                userFilter(query.userId()),
                activeFilter(query.activeOn()),
                dateFrom(query.startDate()),
                dateTo(query.endDate()),
                archived(query.archived())
        );
    }

    private static Specification<Budget> userFilter(Long userId) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("id"), userId);
    }

    private static Specification<Budget> activeFilter(LocalDate activeOn) {
        return (root, query, cb) ->
                activeOn == null ? cb.conjunction() :
                        cb.and(
                                cb.lessThanOrEqualTo(root.get("startDate"), activeOn),
                                cb.greaterThanOrEqualTo(root.get("endDate"), activeOn)
                        );
    }

    private static Specification<Budget> dateFrom(LocalDate startDate) {
        return (root, query, cb) ->
                startDate == null ? cb.conjunction() :
                        cb.equal(root.get("startDate"), startDate);
    }

    private static Specification<Budget> dateTo(LocalDate endDate) {
        return (root, query, cb) ->
                endDate == null ? cb.conjunction() :
                        cb.lessThan(root.get("endDate"), endDate);
    }

    private static Specification<Budget> archived(Boolean archived) {
        return (root, query, cb) ->
                archived == null ? cb.conjunction() :
                        cb.equal(root.get("archived"), archived);
    }
}
