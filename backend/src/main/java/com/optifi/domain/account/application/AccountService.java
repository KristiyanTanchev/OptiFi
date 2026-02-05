package com.optifi.domain.account.application;

import com.optifi.domain.account.application.command.AccountUpdateCommand;
import com.optifi.domain.account.application.command.AccountCreateCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.application.result.AccountSummaryResult;

import java.util.List;

public interface AccountService {
    List<AccountSummaryResult> getAllUserAccounts(long userId);

    AccountDetailsResult createAccount(AccountCreateCommand cmd);

    AccountDetailsResult getAccountById(long accountId, long userId);

    void updateAccount(AccountUpdateCommand cmd);

    void archiveAccount(long accountId, long userId);

    void unarchiveAccount(long accountId, long userId);

    void deleteAccount(long accountId, long userId);
}
