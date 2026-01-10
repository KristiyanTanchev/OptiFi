package com.optifi.domain.transaction.application.command;

import com.optifi.domain.transaction.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionSpecs {

    public static Specification<Transaction> fromQuery(TransactionQuery query) {
        return Specification.allOf(
                userFilter(query.userId()),
                accountFilter(query.accountId()),
                dateFrom(query.from()),
                dateTo(query.to()),
                minAmount(query.min()),
                maxAmount(query.max()),
                descriptionContains(query.description())
        );
    }

    private static Specification<Transaction> userFilter(Long userId) {
        return (root, query, cb) ->
                userId == null ? cb.conjunction() : cb.equal(root.get("account").get("user").get("id"), userId);
    }

    private static Specification<Transaction> accountFilter(Long accountId) {
        return (root, query, cb) ->
                accountId == null ? cb.conjunction() : cb.equal(root.get("account").get("id"), accountId);
    }

    private static Specification<Transaction> dateFrom(LocalDate dateFrom) {
        return (root, query, cb) ->
                dateFrom == null ? cb.conjunction() :
                        cb.greaterThanOrEqualTo(root.get("date"), dateFrom);
    }

    private static Specification<Transaction> dateTo(LocalDate dateTo) {
        return (root, query, cb) ->
                dateTo == null ? cb.conjunction() :
                        cb.lessThanOrEqualTo(root.get("date"), dateTo);
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
}
