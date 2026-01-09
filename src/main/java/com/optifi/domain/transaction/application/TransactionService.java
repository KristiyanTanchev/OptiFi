package com.optifi.domain.transaction.application;

import com.optifi.domain.transaction.application.command.TransactionQuery;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {
    Page<TransactionSummaryResult> getAllUserTransactions(TransactionQuery query, Pageable pageable);

    TransactionDetailsResult getTransaction(long id);

    TransactionDetailsResult createTransaction(long accountId, BigDecimal amount);

    void updateTransaction(long id, BigDecimal amount, String description);

    void deleteTransaction(long id);
}
