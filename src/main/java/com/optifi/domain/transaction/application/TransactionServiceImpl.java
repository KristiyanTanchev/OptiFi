package com.optifi.domain.transaction.application;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.transaction.application.command.*;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
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

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionSummaryResult> getAllUserTransactions(TransactionQuery query, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecs.fromQuery(query);
        Page<Transaction> page = transactionRepository.findAll(spec, pageable);
        return page.map(TransactionSummaryResult::fromEntity);
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
        Transaction transaction = cmd.toEntity(account);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionDetailsResult.fromEntity(savedTransaction);
    }

    @Override
    public void updateTransaction(TransactionUpdateCommand cmd) {
        Transaction transaction = loadTransactionAuthorized(cmd.userId(), cmd.accountId(), cmd.id());
        transaction.setAmount(cmd.amount());
        transaction.setDescription(cmd.description());
        transaction.setOccurredAt(cmd.occurredAt());
    }

    @Override
    public void deleteTransaction(TransactionReferenceCommand cmd) {
        Transaction transaction = loadTransactionAuthorized(cmd.userId(), cmd.accountId(), cmd.transactionId());
        transactionRepository.delete(transaction);
    }

    private Account loadAccountAuthorized(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Account", accountId)
        );
        if (userId.equals(account.getUser().getId())) {
            return account;
        }
        throw new AuthorizationException("You are not authorized to modify this account");
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
}
