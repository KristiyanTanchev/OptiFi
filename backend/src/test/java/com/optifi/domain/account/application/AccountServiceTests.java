package com.optifi.domain.account.application;

import com.optifi.domain.account.application.command.AccountUpdateCommand;
import com.optifi.domain.account.application.command.AccountCreateCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.domain.account.model.Account;
import com.optifi.domain.shared.AccountType;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.shared.Role;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.AuthorizationException;
import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.exceptions.EntityNotFoundException;
import com.optifi.exceptions.IllegalStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account1;
    private Account account2;

    private User user99;
    private User user55;
    private User adminUser;

    @BeforeEach
    void setUp() {

        user99 = User.builder()
                .id(99L)
                .role(Role.USER)
                .build();

        user55 = User.builder()
                .id(55L)
                .role(Role.USER)
                .build();

        adminUser = User.builder()
                .id(1L)
                .role(Role.ADMIN)
                .build();

        account1 = Account.builder()
                .id(1L)
                .name("account1")
                .currency(Currency.EUR)
                .type(AccountType.CASH)
                .user(user99)
                .archived(false)
                .build();

        account2 = Account.builder()
                .id(2L)
                .name("account2")
                .currency(Currency.USD)
                .type(AccountType.BANK)
                .user(user99)
                .archived(false)
                .build();
    }


    @Test
    void getAllUserAccounts_Should_throwException_When_userDoesNotExist() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                EntityNotFoundException.class,
                () -> accountService.getAllUserAccounts(99L)
        );
    }

    @Test
    void getAllUserAccounts_Should_returnAccounts_When_userExists() {
        when(userRepository.existsById(99L)).thenReturn(true);
        when(accountRepository.findAllByUserId(99L)).thenReturn(List.of(account1, account2));

        List<AccountSummaryResult> results = accountService.getAllUserAccounts(99L);

        assertEquals(2, results.size());
        assertEquals(account1.getId(), results.get(0).id());
        assertEquals(account2.getId(), results.get(1).id());
    }

    @Test
    void createAccount_Should_throwException_When_userDoesNotExist() {
        AccountCreateCommand cmd = new AccountCreateCommand(
                99L, "New Account", AccountType.CASH, Currency.EUR, "My Bank"
        );

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> accountService.createAccount(cmd)
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_Should_throwException_When_duplicateName() {
        AccountCreateCommand cmd = new AccountCreateCommand(
                99L, "New Account", AccountType.CASH, Currency.EUR, "My Bank"
        );

        when(userRepository.findById(99L)).thenReturn(Optional.of(user99));
        when(accountRepository.existsByUserIdAndName(99L, "New Account")).thenReturn(true);

        assertThrows(
                DuplicateEntityException.class,
                () -> accountService.createAccount(cmd)
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_Should_saveAndReturnResult_When_valid() {
        AccountCreateCommand cmd = new AccountCreateCommand(
                99L, "New Account", AccountType.CASH, Currency.EUR, "My Bank"
        );

        when(userRepository.findById(99L)).thenReturn(Optional.of(user99));
        when(accountRepository.existsByUserIdAndName(99L, "New Account")).thenReturn(false);

        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(123L);
            return a;
        });

        AccountDetailsResult result = accountService.createAccount(cmd);

        assertEquals(123L, result.id());
        assertEquals("New Account", result.name());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(user99.getId(), captor.getValue().getUser().getId());
        assertEquals("New Account", captor.getValue().getName());
    }

    @Test
    void getAccountById_Should_throwException_When_accountDoesNotExist() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> accountService.getAccountById(1L, 99L)
        );
    }

    @Test
    void getAccountById_Should_throwException_When_accountNotOwnedByUser() {
        User other = User.builder().id(777L).role(Role.USER).build();

        Account otherAccount = Account.builder()
                .id(1L)
                .name("other")
                .currency(Currency.EUR)
                .type(AccountType.CASH)
                .user(other)
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(otherAccount));

        assertThrows(
                EntityNotFoundException.class,
                () -> accountService.getAccountById(1L, 99L)
        );
    }

    @Test
    void getAccountById_Should_returnResult_When_ownedByUser() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));

        AccountDetailsResult result = accountService.getAccountById(1L, 99L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("account1", result.name());
    }

    @Test
    void updateAccount_Should_throwException_When_accountDoesNotExist() {
        AccountUpdateCommand cmd = new AccountUpdateCommand(
                1L, 99L, "updated", AccountType.BANK, Currency.USD, "Inst"
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> accountService.updateAccount(cmd)
        );
    }

    @Test
    void updateAccount_Should_throwException_When_notOwnerAndNotAdmin() {
        AccountUpdateCommand cmd = new AccountUpdateCommand(
                1L, 55L, "updated", AccountType.BANK, Currency.USD, "Inst"
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 55L)).thenReturn(false);
        when(userRepository.findById(55L)).thenReturn(Optional.of(user55));

        assertThrows(
                AuthorizationException.class,
                () -> accountService.updateAccount(cmd)
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_Should_allowAdmin_When_notOwner() {
        AccountUpdateCommand cmd = new AccountUpdateCommand(
                1L, 1L, "updated", AccountType.BANK, Currency.USD, "Inst"
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        when(accountRepository.existsByUserIdAndName(1L, "updated")).thenReturn(false);

        accountService.updateAccount(cmd);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        assertEquals("updated", captor.getValue().getName());
        assertEquals(AccountType.BANK, captor.getValue().getType());
        assertEquals(Currency.USD, captor.getValue().getCurrency());
        assertEquals("Inst", captor.getValue().getInstitution());
    }

    @Test
    void updateAccount_Should_throwException_When_duplicateNameAndNameChanged() {
        AccountUpdateCommand cmd = new AccountUpdateCommand(
                1L, 99L, "newName", AccountType.CASH, Currency.EUR, null
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 99L)).thenReturn(true);
        when(accountRepository.existsByUserIdAndName(99L, "newName")).thenReturn(true);

        assertThrows(
                DuplicateEntityException.class,
                () -> accountService.updateAccount(cmd)
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_Should_saveUpdatedFields_When_ownerAndValid() {
        AccountUpdateCommand cmd = new AccountUpdateCommand(
                1L, 99L, "updated", AccountType.BANK, Currency.USD, "Inst"
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 99L)).thenReturn(true);
        when(accountRepository.existsByUserIdAndName(99L, "updated")).thenReturn(false);

        accountService.updateAccount(cmd);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        assertEquals("updated", captor.getValue().getName());
        assertEquals(AccountType.BANK, captor.getValue().getType());
        assertEquals(Currency.USD, captor.getValue().getCurrency());
        assertEquals("Inst", captor.getValue().getInstitution());
    }


    @Test
    void updateAccount_Should_saveUpdatedFields_When_ownerAndValidandSameName() {
        AccountUpdateCommand cmd = new AccountUpdateCommand(
                1L, 99L, account1.getName(), AccountType.BANK, Currency.USD, "Inst"
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 99L)).thenReturn(true);

        accountService.updateAccount(cmd);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        assertEquals(account1.getName(), captor.getValue().getName());
        assertEquals(AccountType.BANK, captor.getValue().getType());
        assertEquals(Currency.USD, captor.getValue().getCurrency());
        assertEquals("Inst", captor.getValue().getInstitution());
    }

    @Test
    void archiveAccount_Should_throwException_When_alreadyArchived() {
        account1.setArchived(true);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 99L)).thenReturn(true);

        assertThrows(
                IllegalStateTransitionException.class,
                () -> accountService.archiveAccount(1L, 99L)
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void archiveAccount_Should_archiveAndSave_When_valid() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 99L)).thenReturn(true);

        accountService.archiveAccount(1L, 99L);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertTrue(captor.getValue().isArchived());
    }

    @Test
    void unarchiveAccount_Should_throwException_When_notArchived() {
        account1.setArchived(false);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 99L)).thenReturn(true);

        assertThrows(
                IllegalStateTransitionException.class,
                () -> accountService.unarchiveAccount(1L, 99L)
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void unarchiveAccount_Should_unarchiveAndSave_When_valid() {
        account1.setArchived(true);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 99L)).thenReturn(true);

        accountService.unarchiveAccount(1L, 99L);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertFalse(captor.getValue().isArchived());
    }

    @Test
    void deleteAccount_Should_delete_When_owner() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 99L)).thenReturn(true);

        accountService.deleteAccount(1L, 99L);

        verify(accountRepository).delete(account1);
    }

    @Test
    void deleteAccount_Should_delete_When_adminNotOwner() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        accountService.deleteAccount(1L, 1L);

        verify(accountRepository).delete(account1);
    }

    @Test
    void deleteAccount_Should_throwException_When_notOwnerAndNotAdmin() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.existsByIdAndUserId(1L, 55L)).thenReturn(false);
        when(userRepository.findById(55L)).thenReturn(Optional.of(user55));

        assertThrows(
                AuthorizationException.class,
                () -> accountService.deleteAccount(1L, 55L)
        );

        verify(accountRepository, never()).delete(any(Account.class));
    }
}
