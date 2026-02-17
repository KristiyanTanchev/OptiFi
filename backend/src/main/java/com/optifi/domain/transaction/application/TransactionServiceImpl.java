package com.optifi.domain.transaction.application;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.category.model.Category;
import com.optifi.domain.category.repository.CategoryRepository;
import com.optifi.domain.transaction.application.command.*;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionGetSummaryResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import com.optifi.domain.transaction.model.Transaction;
import com.optifi.domain.transaction.repository.TransactionRepository;
import com.optifi.exceptions.AuthorizationException;
import com.optifi.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionSummaryResult> getAllUserTransactions(TransactionQuery query, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecs.fromQuery(query);
        Page<Transaction> page = transactionRepository.findAll(spec, pageable);
        return page.map(TransactionSummaryResult::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTransactionsSum(TransactionQuery query) {
        Specification<Transaction> spec = TransactionSpecs.fromQuery(query);
        return transactionRepository.findAll(spec).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDetailsResult getTransaction(TransactionReferenceCommand cmd) {
        Transaction transaction = loadTransactionAuthorized(cmd.userId(), cmd.accountId(), cmd.transactionId());
        return TransactionDetailsResult.fromEntity(transaction);
    }

    @Override
    public TransactionDetailsResult createTransaction(TransactionCreateCommand cmd) {
        Account account = loadAccountAuthorized(cmd.accountId(), cmd.userId());
        Category category = categoryRepository.findByIdAndUserId(cmd.categoryId(), cmd.userId())
                .orElseThrow(() -> new EntityNotFoundException("category", cmd.categoryId()));
        Transaction transaction = Transaction.builder()
                .account(account)
                .category(category)
                .amount(cmd.amount())
                .description(cmd.description())
                .occurredAt(cmd.occurredAt())
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionDetailsResult.fromEntity(savedTransaction);
    }

    @Override
    public void updateTransaction(TransactionUpdateCommand cmd) {
        Transaction transaction = loadTransactionAuthorized(cmd.userId(), cmd.accountId(), cmd.id());
        Category category = categoryRepository.findByIdAndUserId(cmd.categoryId(), cmd.userId())
                .orElseThrow(() -> new EntityNotFoundException("category", cmd.categoryId()));
        transaction.setCategory(category);
        transaction.setAmount(cmd.amount());
        transaction.setDescription(cmd.description());
        transaction.setOccurredAt(cmd.occurredAt());
    }

    @Override
    public void deleteTransaction(TransactionReferenceCommand cmd) {
        Transaction transaction = loadTransactionAuthorized(cmd.userId(), cmd.accountId(), cmd.transactionId());
        transactionRepository.delete(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionGetSummaryResult getTransactionSummary(TransactionGetSummaryCommand cmd) {
        Account account = loadAccountAuthorized(cmd.accountId(), cmd.userId());

        var p = transactionRepository.getAccountTransactionSummary(
                cmd.userId(),
                cmd.accountId(),
                cmd.from(),
                cmd.to(),
                cmd.categoryId(),
                normalizeQuery(cmd.query())
        );
        return TransactionGetSummaryResult.from(
                account.getCurrency(),
                cmd,
                p
        );
    }

    private Account loadAccountAuthorized(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Account", accountId)
        );
        if (userId.equals(account.getUser().getId())) {
            return account;
        }
        throw new AuthorizationException("You are not authorized to access this account");
    }

    private Transaction loadTransactionAuthorized(Long userId, Long accountId, Long id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Transaction", id)
        );
        if (!accountId.equals(transaction.getAccount().getId())) {
            throw new EntityNotFoundException("Transaction", id);
        }
        if (!userId.equals(transaction.getAccount().getUser().getId())) {
            throw new EntityNotFoundException("Transaction", id);
        }
        return transaction;
    }

    private String normalizeQuery(String q) {
        if (q == null) {
            return null;
        }
        q = q.trim();
        if (q.isEmpty()) {
            return null;
        }
        return q;
    }
}
