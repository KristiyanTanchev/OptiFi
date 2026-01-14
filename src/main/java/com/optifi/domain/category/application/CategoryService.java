package com.optifi.domain.category.application;

import com.optifi.domain.category.application.command.CategoryCreateCommand;
import com.optifi.domain.category.application.command.CategoryUpdateCommand;
import com.optifi.domain.category.application.result.CategoryDetailsResult;
import com.optifi.domain.category.application.result.CategorySummaryResult;

import java.util.List;

public interface CategoryService {
    List<CategorySummaryResult> getUsersCategories(long userId);

    CategoryDetailsResult createCategory(CategoryCreateCommand cmd);

    CategoryDetailsResult getCategoryById(Long categoryId, Long userId);

    void updateCategory(CategoryUpdateCommand cmd);

    void deleteCategory(Long categoryId, Long userId);
}
