package com.optifi.domain.category.repository;

import com.optifi.domain.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId ORDER BY c.name ASC ")
    List<Category> findAllByUserId(long userId);

    boolean existsByNameAndUserId(String name, long userId);
}
