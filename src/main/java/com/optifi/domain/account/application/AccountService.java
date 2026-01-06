package com.optifi.domain.account.application;

import com.optifi.domain.account.application.command.AccountUpdateCommand;
import com.optifi.domain.account.application.command.CreateAccountCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.application.result.AccountSummaryResult;

import java.util.List;

public interface AccountService {
    List<AccountSummaryResult> getAllUserAccounts(long userId);

    AccountDetailsResult createAccount(CreateAccountCommand cmd);

    AccountDetailsResult getAccountById(long accountId, long userId);

    void updateAccount(AccountUpdateCommand cmd);
}
