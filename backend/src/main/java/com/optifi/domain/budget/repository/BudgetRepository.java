package com.optifi.domain.budget.repository;


import com.optifi.domain.budget.model.Budget;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {

    boolean existsByUser_IdAndName(Long aLong, String name);

    Optional<Budget> findByUser_IdAndId(Long userId, Long id);

    boolean existsByUser_IdAndNameAndIdNot(Long userId, String name, Long id);

    @EntityGraph(attributePaths = {"accounts", "categories"})
    @Query("""
                select b from Budget b
                where b.user.id = :userId
                  and b.startDate <= :to
                  and b.endDate >= :from
            """)
    List<Budget> findActiveOverlapping(Long userId, LocalDate from, LocalDate to);
}
