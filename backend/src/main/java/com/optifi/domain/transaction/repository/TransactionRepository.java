package com.optifi.domain.transaction.repository;

import com.optifi.domain.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    @Query("""
            select
              coalesce(sum(case when t.amount > 0 then t.amount else 0 end), 0) as income,
              coalesce(sum(case when t.amount < 0 then -t.amount else 0 end), 0) as expense,
              count(t.id) as count
            from Transaction t
            where t.account.id = :accountId
              and t.account.user.id = :userId
              and (:from is null or t.occurredAt >= :from)
              and (:to is null or t.occurredAt < :to)
              and (:categoryId is null or t.category.id = :categoryId)
              and (:query is null or lower(t.description) like lower(concat('%', :query, '%')))
            """)
    TransactionSummaryProjection getAccountTransactionSummary(
            @Param("userId") Long userId,
            @Param("accountId") Long accountId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("categoryId") Long categoryId,
            @Param("query") String query
    );

    List<TransactionTimeAndAmount> findByAccount_User_IdAndAmountGreaterThanAndOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAt(Long aLong, BigDecimal zero, Instant start, Instant endExclusive);

    List<TransactionTimeAndAmount> findByAccount_User_IdAndAmountLessThanAndOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAt(Long aLong, BigDecimal zero, Instant start, Instant endExclusive);

    List<TransactionTimeAndAmount> findByAccount_User_IdAndOccurredAtGreaterThanEqualAndOccurredAtLessThanOrderByOccurredAt(Long aLong, Instant start, Instant endExclusive);
}
