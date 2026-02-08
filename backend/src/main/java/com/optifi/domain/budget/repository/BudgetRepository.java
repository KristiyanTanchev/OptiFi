package com.optifi.domain.budget.repository;


import com.optifi.domain.budget.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {

    boolean existsByUser_IdAndName(Long aLong, String name);

    Optional<Budget> findByUser_IdAndId(Long userId, Long id);

    boolean existsByUser_IdAndNameAndIdNot(Long userId, String name, Long id);
}
