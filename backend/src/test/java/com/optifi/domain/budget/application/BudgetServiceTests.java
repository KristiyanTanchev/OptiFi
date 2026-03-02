package com.optifi.domain.budget.application;

import com.optifi.domain.account.model.Account;
import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.budget.application.command.BudgetCreateCommand;
import com.optifi.domain.budget.application.command.BudgetEvaluationCommand;
import com.optifi.domain.budget.application.command.BudgetQuery;
import com.optifi.domain.budget.application.command.BudgetUpdateCommand;
import com.optifi.domain.budget.application.result.BudgetDetailsResult;
import com.optifi.domain.budget.model.Budget;
import com.optifi.domain.budget.repository.BudgetRepository;
import com.optifi.domain.category.model.Category;
import com.optifi.domain.category.repository.CategoryRepository;
import com.optifi.domain.shared.BudgetPeriod;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.exceptions.EntityNotFoundException;
import com.optifi.exceptions.IllegalStateTransitionException;
import com.optifi.exceptions.InvalidDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTests {
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private BudgetServiceImpl budgetService;

    private User user;
    private Budget budget11;
    private LocalDate validStartDate, validEndDate, validEndDateWeek;
    private BudgetPeriod validPeriod;
    private Account account1;
    private Category category1;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        budget11 = Budget.builder().id(11L).build();
        validStartDate = LocalDate.of(2018, 1, 1);
        validEndDateWeek = LocalDate.of(2018, 1, 7);
        validEndDate = LocalDate.of(2018, 1, 31);
        validPeriod = BudgetPeriod.MONTH;
        account1 = Account.builder().id(1L).user(user).build();
        category1 = Category.builder().id(1L).build();
    }

    @Test
    void getBudgetsForUser_Should_returnEmptyPage() {
        BudgetQuery query = BudgetQuery.builder()
                .userId(user.getId())
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        when(budgetRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        Page<BudgetDetailsResult> result = budgetService.getBudgetsForUser(query, pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(budgetRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getBudgetById_Should_returnBudget_When_budgetExists() {
        when(budgetRepository.findByUser_IdAndId(user.getId(), budget11.getId())).thenReturn(Optional.of(budget11));
        BudgetDetailsResult result = budgetService.getBudgetById(budget11.getId(), user.getId());
        assertEquals(budget11.getId(), result.id());
    }

    @Test
    void getBudgetById_Should_throw_When_budgetMissing() {
        when(budgetRepository.findByUser_IdAndId(user.getId(), budget11.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> budgetService.getBudgetById(budget11.getId(), user.getId()));
    }

    @Test
    void createBudget_Should_throw_When_userMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> budgetService.createBudget(
                BudgetCreateCommand.builder()
                        .userId(1L)
                        .startDate(validStartDate)
                        .endDate(validEndDate)
                        .budgetPeriod(validPeriod)
                        .build()
        ));
    }

    @Test
    void createBudget_Should_throw_When_nameExists() {
        String budgetName = "Example name";
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), budgetName)).thenReturn(true);
        assertThrows(DuplicateEntityException.class, () -> budgetService.createBudget(
                BudgetCreateCommand.builder()
                        .userId(user.getId())
                        .name(budgetName)
                        .startDate(validStartDate)
                        .endDate(validEndDate)
                        .budgetPeriod(validPeriod)
                        .build()
        ));
    }

    @ParameterizedTest
    @MethodSource("validDateCases")
    void createBudget_Should_notThrow_When_valiDates(LocalDate start, LocalDate end, BudgetPeriod period) {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(start)
                .endDate(end)
                .budgetPeriod(period)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(categoryRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> budgetService.createBudget(cmd));
    }

    static Stream<Arguments> validDateCases() {
        LocalDate validStartDate = LocalDate.of(2018, 1, 1);
        LocalDate validEndDateWeek = LocalDate.of(2018, 1, 7);
        LocalDate validEndDateMonth = LocalDate.of(2018, 1, 31);
        LocalDate validEndDateYear = LocalDate.of(2018, 12, 31);
        return Stream.of(
                Arguments.of(validStartDate, validEndDateWeek, BudgetPeriod.WEEK),
                Arguments.of(validStartDate, validEndDateMonth, BudgetPeriod.MONTH),
                Arguments.of(validStartDate, validEndDateYear, BudgetPeriod.YEAR)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDateCases")
    void createBudget_Should_throw_When_invalidDates(
            LocalDate start, LocalDate end, BudgetPeriod period
    ) {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Any")
                .startDate(start)
                .endDate(end)
                .budgetPeriod(period)
                .build();

        assertThrows(InvalidDateException.class, () -> budgetService.createBudget(cmd));
    }

    static Stream<Arguments> invalidDateCases() {
        LocalDate validStartDate = LocalDate.of(2018, 1, 1);
        LocalDate validEndDateWeek = LocalDate.of(2018, 1, 7);
        LocalDate validEndDateMonth = LocalDate.of(2018, 1, 31);
        LocalDate validEndDateYear = LocalDate.of(2018, 12, 31);

        return Stream.of(
                // start after end
                Arguments.of(validEndDateMonth, validStartDate, BudgetPeriod.MONTH),

                // WEEK: start not Monday
                Arguments.of(validStartDate.plusDays(1), validEndDateWeek, BudgetPeriod.WEEK),

                // WEEK: end not Sunday
                Arguments.of(validStartDate, validEndDateWeek.minusDays(1), BudgetPeriod.WEEK),

                // WEEK: not same week (not exactly +6)
                Arguments.of(validStartDate, validEndDateWeek.plusWeeks(1), BudgetPeriod.WEEK),

                // MONTH: start not first
                Arguments.of(validStartDate.plusDays(1), validEndDateMonth, BudgetPeriod.MONTH),

                // MONTH: end not last
                Arguments.of(validStartDate, validEndDateMonth.minusDays(1), BudgetPeriod.MONTH),

                // MONTH: not same month
                Arguments.of(validStartDate, validEndDateMonth.plusMonths(1), BudgetPeriod.MONTH),

                // YEAR: start not first day of year
                Arguments.of(validStartDate.plusDays(1), validEndDateYear, BudgetPeriod.YEAR),

                // YEAR: end not last
                Arguments.of(validStartDate, validEndDateYear.minusDays(1), BudgetPeriod.YEAR),

                // YEAR: not same year
                Arguments.of(validStartDate, validEndDateYear.plusYears(1), BudgetPeriod.YEAR)
        );
    }


    //resolveAccounts tests start
    @Test
    void createBudget_Should_throw_When_accountIdsContainsNotFound() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .accountIds(java.util.List.of(1L, 11L))
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllById(cmd.accountIds())).thenReturn(List.of(account1));

        assertThrows(EntityNotFoundException.class, () -> budgetService.createBudget(cmd));
    }

    @Test
    void createBudget_Should_throw_When_accountIdsContainsNotOwnedAccount() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .accountIds(java.util.List.of(1L))
                .build();

        account1.setUser(User.builder().id(2L).build());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllById(cmd.accountIds())).thenReturn(List.of(account1));

        assertThrows(EntityNotFoundException.class, () -> budgetService.createBudget(cmd));
    }

    @Test
    void createBudget_Should_succeed_When_accountIdsIsNull() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .accountIds(null)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(categoryRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> budgetService.createBudget(cmd));
    }

    @Test
    void createBudget_Should_succeed_When_accountIdsIsEmptyList() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .accountIds(List.of())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(categoryRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> budgetService.createBudget(cmd));
    }

    @Test
    void createBudget_Should_succeed_When_accountIdsIsCorrectList() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .accountIds(java.util.List.of(1L))
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllById(cmd.accountIds())).thenReturn(List.of(account1));
        when(categoryRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> budgetService.createBudget(cmd));
    }
    //resolveAccounts tests end


    //resolveCategories tests start
    @Test
    void createBudget_Should_throw_When_categoryIdsContainsNotFound() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .categoryIds(java.util.List.of(1L, 11L))
                .build();
        category1.setUser(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(categoryRepository.findAllById(cmd.categoryIds())).thenReturn(List.of(category1));

        assertThrows(EntityNotFoundException.class, () -> budgetService.createBudget(cmd));
    }

    @Test
    void createBudget_Should_throw_When_categoryIdsContainsNotOwnedCategory() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .categoryIds(java.util.List.of(1L))
                .build();

        category1.setUser(User.builder().id(2L).build());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(categoryRepository.findAllById(cmd.categoryIds())).thenReturn(List.of(category1));

        assertThrows(EntityNotFoundException.class, () -> budgetService.createBudget(cmd));
    }

    @Test
    void createBudget_Should_succeed_When_categoryIdsIsNull() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .categoryIds(null)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(categoryRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> budgetService.createBudget(cmd));
    }

    @Test
    void createBudget_Should_succeed_When_categoryIdsIsEmptyList() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .categoryIds(List.of())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(categoryRepository.findAllByUserId(user.getId())).thenReturn(java.util.List.of());
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> budgetService.createBudget(cmd));
    }

    @Test
    void createBudget_Should_succeed_When_categoryIdsIsCorrectList() {
        BudgetCreateCommand cmd = BudgetCreateCommand.builder()
                .userId(user.getId())
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .budgetPeriod(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .categoryIds(java.util.List.of(1L))
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(budgetRepository.existsByUser_IdAndName(user.getId(), "Budget")).thenReturn(false);
        when(accountRepository.findAllByUserId(user.getId())).thenReturn(List.of());
        when(categoryRepository.findAllById(cmd.categoryIds())).thenReturn(java.util.List.of(category1));
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> budgetService.createBudget(cmd));
    }
    //resolveCategories tests end

    @Test
    void updateBudget_Should_throw_When_BudgetNotFound() {
        BudgetUpdateCommand cmd = BudgetUpdateCommand.builder()
                .userId(1L)
                .budgetId(11L)
                .build();
        assertThrows(EntityNotFoundException.class, () -> budgetService.updateBudget(cmd));
        verify(budgetRepository).findByUser_IdAndId(1L, 11L);
    }

    @Test
    void updateBudget_Should_throw_When_nameExists() {
        String budgetName = "Example name";
        when(budgetRepository.findByUser_IdAndId(user.getId(), budget11.getId())).thenReturn(Optional.of(budget11));
        when(budgetRepository.existsByUser_IdAndNameAndIdNot(user.getId(), budgetName, budget11.getId())).thenReturn(true);
        BudgetUpdateCommand cmd = BudgetUpdateCommand.builder()
                .userId(user.getId())
                .budgetId(11L)
                .name(budgetName)
                .build();
        assertThrows(DuplicateEntityException.class, () -> budgetService.updateBudget(cmd));
    }

    @Test
    void updateBudget_Should_succeed_When_valid() {
        Budget budget = Budget.builder()
                .id(11L)
                .name("Budget")
                .startDate(validStartDate)
                .endDate(validEndDate)
                .period(validPeriod)
                .amount(BigDecimal.TEN)
                .currency(Currency.USD)
                .user(user)
                .build();
        BudgetUpdateCommand cmd = BudgetUpdateCommand.builder()
                .userId(1L)
                .budgetId(11L)
                .budgetPeriod(BudgetPeriod.WEEK)
                .startDate(validStartDate)
                .endDate(validEndDateWeek)
                .name("Updated")
                .amount(BigDecimal.ONE)
                .currency(Currency.EUR)
                .build();
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.of(budget));
        budgetService.updateBudget(cmd);
        verify(budgetRepository).findByUser_IdAndId(1L, 11L);
        verify(budgetRepository).save(budget);
    }

    @Test
    void archiveBudget_Should_throw_When_budgetNotFound() {
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> budgetService.archiveBudget(11L, 1L));
    }

    @Test
    void archiveBudget_Should_throw_When_budgetArchived() {
        budget11.setArchived(true);
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.of(budget11));
        assertThrows(IllegalStateTransitionException.class, () -> budgetService.archiveBudget(11L, 1L));
    }

    @Test
    void archiveBudget_Should_succeed_When_valid() {
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.of(budget11));
        assertDoesNotThrow(() -> budgetService.archiveBudget(11L, 1L));
        verify(budgetRepository).findByUser_IdAndId(1L, 11L);
        verify(budgetRepository).save(budget11);
    }


    @Test
    void unarchiveBudget_Should_throw_When_budgetNotFound() {
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> budgetService.unarchiveBudget(11L, 1L));
    }

    @Test
    void unarchiveBudget_Should_throw_When_budgetArchived() {
        budget11.setArchived(false);
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.of(budget11));
        assertThrows(IllegalStateTransitionException.class, () -> budgetService.unarchiveBudget(11L, 1L));
    }

    @Test
    void unarchiveBudget_Should_succeed_When_valid() {
        budget11.setArchived(true);
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.of(budget11));
        assertDoesNotThrow(() -> budgetService.unarchiveBudget(11L, 1L));
        verify(budgetRepository).findByUser_IdAndId(1L, 11L);
        verify(budgetRepository).save(budget11);
    }

    @Test
    void deleteBudget_Should_throw_When_budgetNotFound() {
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> budgetService.deleteBudget(11L, 1L));
    }

    @Test
    void deleteBudget_Should_succeed_When_valid() {
        when(budgetRepository.findByUser_IdAndId(1L, 11L)).thenReturn(Optional.of(budget11));
        assertDoesNotThrow(() -> budgetService.deleteBudget(11L, 1L));
        verify(budgetRepository).findByUser_IdAndId(1L, 11L);
        verify(budgetRepository).delete(budget11);
    }

    @Test
    void evaluateBudget_Should_throw_When_endDateBeforeStartDate() {
        BudgetEvaluationCommand cmd = BudgetEvaluationCommand.builder()
                .userId(1L)
                .from(validEndDate).to(validStartDate)
                .build();
        assertThrows(InvalidDateException.class, () -> budgetService.evaluateBudget(cmd));
    }

    @Test
    void evaluateBudget_Should_succeed_When_valid() {
        BudgetEvaluationCommand cmd = BudgetEvaluationCommand.builder()
                .userId(1L)
                .from(validStartDate).to(validEndDate)
                .build();
        Budget budget22 = Budget.builder()
                .id(22L)
                .accounts(new HashSet<>(List.of(account1)))
                .categories(new HashSet<>(List.of(category1)))
                .build();
        when(budgetRepository.findActiveOverlapping(
                cmd.userId(), cmd.from(), cmd.to()
        )).thenReturn(List.of(budget11, budget22));
        assertDoesNotThrow(() -> budgetService.evaluateBudget(cmd));
    }
}
