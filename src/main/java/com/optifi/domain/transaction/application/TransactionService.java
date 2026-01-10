package com.optifi.domain.transaction.application;

import com.optifi.domain.transaction.application.command.TransactionQuery;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {
    Page<TransactionSummaryResult> getAllUserTransactions(TransactionQuery query, Pageable pageable);

    TransactionDetailsResult getTransaction(Long id, Long userId);

    TransactionDetailsResult createTransaction(Long accountId, BigDecimal amount);

    void updateTransaction(Long id, BigDecimal amount, String description);

    void deleteTransaction(Long id);
}
