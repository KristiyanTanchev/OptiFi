package com.optifi.config;

import com.optifi.domain.category.model.Category;
import com.optifi.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultCategorySeeder implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    private static final List<SeedCategory> DEFAULTS = List.of(
            new SeedCategory("Food & drinks","Groceries, restaurants, coffee, takeout","restaurant"),
            new SeedCategory("Shopping","Clothes, gadgets, household shopping","shopping_cart"),
            new SeedCategory("Housing","Rent, utilities, home services","home"),
            new SeedCategory("Transportation","Public transport, taxi, commute","directions_bus"),
            new SeedCategory("Vehicle","Fuel, maintenance, repairs, car costs","directions_car"),
            new SeedCategory("Life & Entertainment","Movies, fun, hobbies, events","movie"),
            new SeedCategory("Communication, PC", "Internet, phone, software, devices", "computer"),
            new SeedCategory("Financial expenses","Fees, charges, subscriptions, interest","receipt_long"),
            new SeedCategory("Investments","Stocks, ETFs, crypto, investing","trending_up"),
            new SeedCategory("Income","Salary and other income","attach_money")
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Seeding default categories…");

        int created = 0;

        for (SeedCategory s : DEFAULTS) {
            boolean exists = categoryRepository.existsByUserIsNullAndNameIgnoreCase(s.name());
            if (!exists) {
                Category c = Category.createDefault(s.name(), s.description(), s.icon());
                categoryRepository.save(c);
                created++;
            }
        }

        log.info("Default categories seed complete. Created {}", created);
    }

    private record SeedCategory(String name, String description, String icon) {}
}
