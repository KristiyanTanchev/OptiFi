package com.optifi.domain.account.application;

import com.optifi.domain.account.application.command.CreateAccountCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.model.Account;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.exceptions.EntityNotFoundException;
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
    public AccountDetailsResult createAccount(CreateAccountCommand cmd) {
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

}
