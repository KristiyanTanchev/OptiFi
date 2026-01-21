package com.optifi.domain.transaction.application;

import com.optifi.domain.transaction.application.command.*;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionGetSummaryResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    Page<TransactionSummaryResult> getAllUserTransactions(TransactionQuery query, Pageable pageable);

    TransactionDetailsResult getTransaction(TransactionReferenceCommand cmd);

    TransactionDetailsResult createTransaction(TransactionCreateCommand cmd);

    void updateTransaction(TransactionUpdateCommand cmd);

    void deleteTransaction(TransactionReferenceCommand cmd);

    TransactionGetSummaryResult getTransactionSummary(TransactionGetSummaryCommand cmd);
}
