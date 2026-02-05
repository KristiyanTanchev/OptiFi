package com.optifi.domain.account.application;

import com.optifi.domain.account.application.command.AccountUpdateCommand;
import com.optifi.domain.account.application.command.AccountCreateCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.model.Account;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.AuthorizationException;
import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.exceptions.EntityNotFoundException;
import com.optifi.exceptions.IllegalStateTransitionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AccountSummaryResult> getAllUserAccounts(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }

        List<Account> accounts = accountRepository.findAllByUserId(userId);
        return accounts.stream().map(AccountSummaryResult::fromEntity).toList();
    }

    @Override
    public AccountDetailsResult createAccount(AccountCreateCommand cmd) {
        User user = userRepository.findById(cmd.userId()).orElseThrow(
                () -> new EntityNotFoundException("User", cmd.userId())
        );

        if (accountRepository.existsByUserIdAndName(cmd.userId(), cmd.name())) {
            throw new DuplicateEntityException("Account", "name", cmd.name());
        }

        Account account = Account.builder()
                .user(user)
                .currency(cmd.currency())
                .institution(cmd.institution())
                .name(cmd.name())
                .type(cmd.type())
                .build();
        return AccountDetailsResult.fromEntity(accountRepository.save(account));
    }

    @Override
    public AccountDetailsResult getAccountById(long accountId, long userId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new EntityNotFoundException("Account", accountId)
        );
        if (!account.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Account", accountId);
        }
        return AccountDetailsResult.fromEntity(account);
    }

    @Override
    public void updateAccount(AccountUpdateCommand cmd) {
        Account account = loadAccountAuthorized(cmd.accountId(), cmd.userId());
        if (!account.getName().equals(cmd.name()) &&
                accountRepository.existsByUserIdAndName(cmd.userId(), cmd.name())) {
            throw new DuplicateEntityException("Account", "name", cmd.name());
        }
        account.setName(cmd.name());
        account.setType(cmd.type());
        account.setCurrency(cmd.currency());
        account.setInstitution(cmd.institution());
        accountRepository.save(account);
    }

    @Override
    public void archiveAccount(long accountId, long userId) {
        Account account = loadAccountAuthorized(accountId, userId);
        if (account.isArchived()) {
            throw new IllegalStateTransitionException("Account is already archived");
        }
        account.setArchived(true);
        accountRepository.save(account);
    }

    @Override
    public void unarchiveAccount(long accountId, long userId) {
        Account account = loadAccountAuthorized(accountId, userId);
        if (!account.isArchived()) {
            throw new IllegalStateTransitionException("Account is not archived");
        }
        account.setArchived(false);
        accountRepository.save(account);
    }

    @Override
    public void deleteAccount(long accountId, long userId) {
        Account account = loadAccountAuthorized(accountId, userId);
        accountRepository.delete(account);
    }

    private Account loadAccountAuthorized(long accountId, long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account", accountId));

        if (accountRepository.existsByIdAndUserId(accountId, userId)) {
            return account;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        if (!user.isAdmin()) {
            throw new AuthorizationException("You cannot modify this account");
        }

        return account;
    }

}
