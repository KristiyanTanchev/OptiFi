package com.optifi.domain.transaction.application.command;

import com.optifi.domain.transaction.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class TransactionSpecs {

    public static Specification<Transaction> fromQuery(TransactionQuery query) {
        return Specification.allOf(
                userFilter(query.userId()),
                accountFilter(query.accountId()),
                dateFrom(query.from()),
                dateTo(query.to()),
                minAmount(query.min()),
                maxAmount(query.max()),
                descriptionContains(query.description()),
                accountsFilter(query.accountIds()),
                categoriesFilter(query.categoryIds())
        );
    }

    private static Specification<Transaction> userFilter(Long userId) {
        return (root, query, cb) ->
                cb.equal(root.get("account").get("user").get("id"), userId);
    }

    private static Specification<Transaction> accountFilter(Long accountId) {
        return (root, query, cb) ->
                cb.equal(root.get("account").get("id"), accountId);
    }

    private static Specification<Transaction> dateFrom(Instant dateFrom) {
        return (root, query, cb) ->
                dateFrom == null ? cb.conjunction() :
                        cb.greaterThanOrEqualTo(root.get("occurredAt"), dateFrom);
    }

    private static Specification<Transaction> dateTo(Instant dateTo) {
        return (root, query, cb) ->
                dateTo == null ? cb.conjunction() :
                        cb.lessThan(root.get("occurredAt"), dateTo);
    }

    private static Specification<Transaction> minAmount(BigDecimal minAmount) {
        return (root, query, cb) ->
                minAmount == null ? cb.conjunction() :
                        cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
    }

    private static Specification<Transaction> maxAmount(BigDecimal maxAmount) {
        return (root, query, cb) ->
                maxAmount == null ? cb.conjunction() :
                        cb.lessThanOrEqualTo(root.get("amount"), maxAmount);
    }

    private static Specification<Transaction> descriptionContains(String description) {
        return (root, query, cb) ->
                description == null || description.isBlank() ? cb.conjunction() :
                        cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<Transaction> accountsFilter(List<Long> accountIds) {
        return (root, query, cb) ->
                accountIds == null || accountIds.isEmpty() ? cb.conjunction() :
                        cb.in(root.get("account").get("id")).value(accountIds);
    }

    private static Specification<Transaction> categoriesFilter(List<Long> categoryIds) {
        return (root, query, cb) ->
                categoryIds == null || categoryIds.isEmpty() ? cb.conjunction() :
                        cb.in(root.get("category").get("id")).value(categoryIds);
    }
}
