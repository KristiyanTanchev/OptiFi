package com.optifi.integration.support;

import com.optifi.domain.account.repository.AccountRepository;
import com.optifi.domain.budget.repository.BudgetRepository;
import com.optifi.domain.category.repository.CategoryRepository;
import com.optifi.domain.transaction.repository.TransactionRepository;
import com.optifi.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected MockMvc mockMvc;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("optifi_test")
            .withUsername("optifi")
            .withPassword("optifi");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @AfterEach
    void cleanUp() {
        accountRepository.deleteAll();
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }
}