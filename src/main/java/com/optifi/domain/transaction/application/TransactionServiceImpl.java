package com.optifi.domain.transaction.application;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.transaction.application.command.TransactionQuery;
import com.optifi.domain.transaction.application.command.TransactionSpecs;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import com.optifi.domain.transaction.model.Transaction;
import com.optifi.domain.transaction.repository.TransactionRepository;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public Page<TransactionSummaryResult> getAllUserTransactions(TransactionQuery query, Pageable pageable) {
        loadAccountAuthorized(query.accountId(), query.userId());
        Specification<Transaction> spec = TransactionSpecs.fromQuery(query);
        Page<Transaction> page = transactionRepository.findAll(spec, pageable);
        return page.map(TransactionSummaryResult::fromEntity);
    }

    @Override
    public TransactionDetailsResult getTransaction(Long id, Long userId) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Transaction", id)
        );
        if (!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new AuthorizationException("You are not authorized to view this transaction");
        }
        return TransactionDetailsResult.fromEntity(transaction);
    }

    @Override
    public TransactionDetailsResult createTransaction(Long accountId, BigDecimal amount) {
        return null;
    }

    @Override
    public void updateTransaction(Long id, BigDecimal amount, String description) {

    }

    @Override
    public void deleteTransaction(Long id) {

    }

    private Account loadAccountAuthorized(Long accountId, Long userId) {
        User requestedUser = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User", userId)
        );
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Account", accountId)
        );
        if (requestedUser.getId().equals(account.getUser().getId())) {
            return account;
        }
        throw new AuthorizationException("You are not authorized to modify this account");
    }
}
