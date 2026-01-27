package com.optifi.domain.transaction.application;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.category.model.Category;
import com.optifi.domain.category.repository.CategoryRepository;
import com.optifi.domain.shared.model.Currency;
import com.optifi.domain.transaction.application.command.*;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionGetSummaryResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import com.optifi.domain.transaction.model.Transaction;
import com.optifi.domain.transaction.repository.TransactionRepository;
import com.optifi.domain.transaction.repository.TransactionSummaryProjection;
import com.optifi.domain.user.model.User;
import com.optifi.exceptions.AuthorizationException;
import com.optifi.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account account1;
    private Account otherAccount;

    private Category category1;

    private Transaction tx1;

    @BeforeEach
    void setUp() {
        // IMPORTANT: no stubbing in setUp()

        User user99 = User.builder().id(99L).build();
        User otherUser = User.builder().id(55L).build();

        account1 = Account.builder()
                .id(10L)
                .user(user99)
                .currency(Currency.EUR)
                .name("acc")
                .build();

        otherAccount = Account.builder()
                .id(11L)
                .user(otherUser)
                .currency(Currency.USD)
                .name("other")
                .build();

        category1 = Category.builder()
                .id(20L)
                .user(user99)
                .name("Food")
                .build();

        tx1 = Transaction.builder()
                .id(100L)
                .account(account1)
                .category(category1)
                .amount(new BigDecimal("12.34"))
                .description("Lunch")
                .occurredAt(Instant.parse("2026-01-01T10:00:00Z"))
                .build();
    }

    // -------------------------
    // getAllUserTransactions
    // -------------------------

    @Test
    void getAllUserTransactions_Should_returnMappedPage() {
        TransactionQuery query = new TransactionQuery(
                99L,
                10L,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-02-01T00:00:00Z"),
                new BigDecimal("1.00"),
                new BigDecimal("100.00"),
                "lunch"
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("occurredAt").descending());
        Page<Transaction> page = new PageImpl<>(List.of(tx1), pageable, 1);

        when(transactionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<TransactionSummaryResult> results = transactionService.getAllUserTransactions(query, pageable);

        assertEquals(1, results.getTotalElements());
        assertEquals(tx1.getId(), results.getContent().get(0).id());

        verify(transactionRepository).findAll(any(Specification.class), eq(pageable));
    }

    // -------------------------
    // getTransaction
    // -------------------------

    @Test
    void getTransaction_Should_throwException_When_transactionNotFound() {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(99L, 10L, 100L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransaction(cmd));
    }

    @Test
    void getTransaction_Should_throwException_When_accountIdMismatch() {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(99L, 999L, 100L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransaction(cmd));
    }

    @Test
    void getTransaction_Should_throwException_When_userIdMismatch() {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(55L, 10L, 100L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransaction(cmd));
    }

    @Test
    void getTransaction_Should_returnDetails_When_authorized() {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(99L, 10L, 100L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));

        TransactionDetailsResult result = transactionService.getTransaction(cmd);

        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(10L, result.accountId());
        assertEquals(new BigDecimal("12.34"), result.amount());
        assertEquals("Lunch", result.description());
        assertEquals(Instant.parse("2026-01-01T10:00:00Z"), result.occurredAt());
    }

    // -------------------------
    // createTransaction
    // -------------------------

    @Test
    void createTransaction_Should_throwException_When_accountNotFound() {
        TransactionCreateCommand cmd = new TransactionCreateCommand(
                99L,
                10L,
                new BigDecimal("5.00"),
                "Coffee",
                Instant.parse("2026-01-02T10:00:00Z"),
                20L
        );

        when(accountRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.createTransaction(cmd));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_Should_throwException_When_notAccountOwner() {
        TransactionCreateCommand cmd = new TransactionCreateCommand(
                99L,
                11L,
                new BigDecimal("5.00"),
                "Coffee",
                Instant.parse("2026-01-02T10:00:00Z"),
                20L
        );

        when(accountRepository.findById(11L)).thenReturn(Optional.of(otherAccount));

        assertThrows(AuthorizationException.class, () -> transactionService.createTransaction(cmd));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_Should_throwException_When_categoryNotFoundForUser() {
        TransactionCreateCommand cmd = new TransactionCreateCommand(
                99L,
                10L,
                new BigDecimal("5.00"),
                "Coffee",
                Instant.parse("2026-01-02T10:00:00Z"),
                20L
        );

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account1));
        when(categoryRepository.findByIdAndUserId(20L, 99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.createTransaction(cmd));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_Should_saveAndReturnDetails_When_valid() {
        TransactionCreateCommand cmd = new TransactionCreateCommand(
                99L,
                10L,
                new BigDecimal("5.00"),
                "Coffee",
                Instant.parse("2026-01-02T10:00:00Z"),
                20L
        );

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account1));
        when(categoryRepository.findByIdAndUserId(20L, 99L)).thenReturn(Optional.of(category1));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(777L);
            return t;
        });

        TransactionDetailsResult result = transactionService.createTransaction(cmd);

        assertNotNull(result);
        assertEquals(777L, result.id());
        assertEquals(10L, result.accountId());
        assertEquals(new BigDecimal("5.00"), result.amount());
        assertEquals("Coffee", result.description());
        assertEquals(Instant.parse("2026-01-02T10:00:00Z"), result.occurredAt());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());

        assertEquals(account1.getId(), captor.getValue().getAccount().getId());
        assertEquals(category1.getId(), captor.getValue().getCategory().getId());
        assertEquals(new BigDecimal("5.00"), captor.getValue().getAmount());
        assertEquals("Coffee", captor.getValue().getDescription());
        assertEquals(Instant.parse("2026-01-02T10:00:00Z"), captor.getValue().getOccurredAt());
    }

    // -------------------------
    // updateTransaction
    // -------------------------

    @Test
    void updateTransaction_Should_throwException_When_transactionNotFound() {
        TransactionUpdateCommand cmd = new TransactionUpdateCommand(
                99L,
                10L,
                100L,
                new BigDecimal("1.00"),
                "x",
                Instant.parse("2026-01-03T10:00:00Z"),
                20L
        );

        when(transactionRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.updateTransaction(cmd));
    }

    @Test
    void updateTransaction_Should_throwException_When_accountIdMismatch() {
        TransactionUpdateCommand cmd = new TransactionUpdateCommand(
                99L,
                999L,
                100L,
                new BigDecimal("1.00"),
                "x",
                Instant.parse("2026-01-03T10:00:00Z"),
                20L
        );

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));

        assertThrows(EntityNotFoundException.class, () -> transactionService.updateTransaction(cmd));
    }

    @Test
    void updateTransaction_Should_throwException_When_userIdMismatch() {
        TransactionUpdateCommand cmd = new TransactionUpdateCommand(
                55L,
                10L,
                100L,
                new BigDecimal("1.00"),
                "x",
                Instant.parse("2026-01-03T10:00:00Z"),
                20L
        );

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));

        assertThrows(EntityNotFoundException.class, () -> transactionService.updateTransaction(cmd));
    }

    @Test
    void updateTransaction_Should_throwException_When_categoryNotFoundForUser() {
        TransactionUpdateCommand cmd = new TransactionUpdateCommand(
                99L,
                10L,
                100L,
                new BigDecimal("1.00"),
                "x",
                Instant.parse("2026-01-03T10:00:00Z"),
                20L
        );

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));
        when(categoryRepository.findByIdAndUserId(20L, 99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.updateTransaction(cmd));
    }

    @Test
    void updateTransaction_Should_updateFields_When_valid() {
        TransactionUpdateCommand cmd = new TransactionUpdateCommand(
                99L,
                10L,
                100L,
                new BigDecimal("99.99"),
                "Updated",
                Instant.parse("2026-01-03T10:00:00Z"),
                20L
        );

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));
        when(categoryRepository.findByIdAndUserId(20L, 99L)).thenReturn(Optional.of(category1));

        transactionService.updateTransaction(cmd);

        assertEquals(category1.getId(), tx1.getCategory().getId());
        assertEquals(new BigDecimal("99.99"), tx1.getAmount());
        assertEquals("Updated", tx1.getDescription());
        assertEquals(Instant.parse("2026-01-03T10:00:00Z"), tx1.getOccurredAt());

        // relies on transactional dirty checking
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // -------------------------
    // deleteTransaction
    // -------------------------

    @Test
    void deleteTransaction_Should_throwException_When_transactionNotFound() {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(99L, 10L, 100L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.deleteTransaction(cmd));
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void deleteTransaction_Should_throwException_When_accountIdMismatch() {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(99L, 999L, 100L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));

        assertThrows(EntityNotFoundException.class, () -> transactionService.deleteTransaction(cmd));
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void deleteTransaction_Should_throwException_When_userIdMismatch() {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(55L, 10L, 100L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));

        assertThrows(EntityNotFoundException.class, () -> transactionService.deleteTransaction(cmd));
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void deleteTransaction_Should_delete_When_authorized() {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(99L, 10L, 100L);

        when(transactionRepository.findById(100L)).thenReturn(Optional.of(tx1));

        transactionService.deleteTransaction(cmd);

        verify(transactionRepository).delete(tx1);
    }

    // -------------------------
    // getTransactionSummary (Instant + normalizeQuery)
    // -------------------------

    @Test
    void getTransactionSummary_Should_throwException_When_accountNotFound() {
        TransactionGetSummaryCommand cmd = new TransactionGetSummaryCommand(
                99L,
                10L,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-31T23:59:59Z"),
                null,
                "coffee"
        );

        when(accountRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionSummary(cmd));
    }

    @Test
    void getTransactionSummary_Should_throwException_When_notAccountOwner() {
        TransactionGetSummaryCommand cmd = new TransactionGetSummaryCommand(
                99L,
                11L,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-31T23:59:59Z"),
                null,
                "coffee"
        );

        when(accountRepository.findById(11L)).thenReturn(Optional.of(otherAccount));

        assertThrows(AuthorizationException.class, () -> transactionService.getTransactionSummary(cmd));
    }

    @Test
    void getTransactionSummary_Should_trimQuery_When_hasSpaces() {
        TransactionGetSummaryCommand cmd = new TransactionGetSummaryCommand(
                99L,
                10L,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-31T23:59:59Z"),
                null,
                "   coffee   "
        );

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account1));

        TransactionSummaryProjection projection = mock(TransactionSummaryProjection.class);
        when(projection.getIncome()).thenReturn(new BigDecimal("10.00"));
        when(projection.getExpense()).thenReturn(new BigDecimal("3.00"));
        when(projection.getCount()).thenReturn(2L);

        when(transactionRepository.getAccountTransactionSummary(
                eq(99L),
                eq(10L),
                eq(cmd.from()),
                eq(cmd.to()),
                isNull(),
                eq("coffee")
        )).thenReturn(projection);

        TransactionGetSummaryResult result = transactionService.getTransactionSummary(cmd);

        assertNotNull(result);
        assertEquals(10L, result.accountId());
        assertEquals(Currency.EUR.name(), result.currency());
        assertEquals(new BigDecimal("10.00"), result.income());
        assertEquals(new BigDecimal("3.00"), result.expense());
        assertEquals(new BigDecimal("7.00"), result.net());
        assertEquals(2L, result.count());

        verify(transactionRepository).getAccountTransactionSummary(
                eq(99L),
                eq(10L),
                eq(cmd.from()),
                eq(cmd.to()),
                isNull(),
                eq("coffee")
        );
    }

    @Test
    void getTransactionSummary_Should_passNullQuery_When_blank() {
        TransactionGetSummaryCommand cmd = new TransactionGetSummaryCommand(
                99L,
                10L,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-31T23:59:59Z"),
                null,
                "   "
        );

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account1));

        TransactionSummaryProjection projection = mock(TransactionSummaryProjection.class);
        when(projection.getIncome()).thenReturn(new BigDecimal("0.00"));
        when(projection.getExpense()).thenReturn(new BigDecimal("0.00"));
        when(projection.getCount()).thenReturn(0L);

        when(transactionRepository.getAccountTransactionSummary(
                eq(99L),
                eq(10L),
                eq(cmd.from()),
                eq(cmd.to()),
                isNull(),
                isNull()
        )).thenReturn(projection);

        TransactionGetSummaryResult result = transactionService.getTransactionSummary(cmd);

        assertNotNull(result);

        verify(transactionRepository).getAccountTransactionSummary(
                eq(99L),
                eq(10L),
                eq(cmd.from()),
                eq(cmd.to()),
                isNull(),
                isNull()
        );
    }
}
