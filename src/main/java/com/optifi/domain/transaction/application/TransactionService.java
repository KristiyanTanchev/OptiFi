package com.optifi.domain.transaction.application;

import com.optifi.domain.transaction.application.command.TransactionCreateCommand;
import com.optifi.domain.transaction.application.command.TransactionQuery;
import com.optifi.domain.transaction.application.command.TransactionUpdateCommand;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    Page<TransactionSummaryResult> getAllUserTransactions(TransactionQuery query, Pageable pageable);

    TransactionDetailsResult getTransaction(Long id, Long userId);

    TransactionDetailsResult createTransaction(TransactionCreateCommand cmd);

    void updateTransaction(TransactionUpdateCommand cmd);

    void deleteTransaction(Long id, Long userId);
}
