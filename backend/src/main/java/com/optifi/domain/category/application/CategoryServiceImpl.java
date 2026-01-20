package com.optifi.domain.category.application;

import com.optifi.config.FeatureProperties;
import com.optifi.domain.category.application.command.CategoryCreateCommand;
import com.optifi.domain.category.application.command.CategoryUpdateCommand;
import com.optifi.domain.category.application.result.CategoryDetailsResult;
import com.optifi.domain.category.application.result.CategorySummaryResult;
import com.optifi.domain.category.model.Category;
import com.optifi.domain.category.repository.CategoryRepository;
import com.optifi.domain.user.model.Role;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.AuthorizationException;
import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FeatureProperties featureProperties;

    @Override
    public List<CategorySummaryResult> getUsersCategories(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
        List<Category> categories = categoryRepository.findAllByUserId(userId);
        return categories.stream().map(CategorySummaryResult::fromEntity).toList();
    }

    @Override
    public CategoryDetailsResult createCategory(CategoryCreateCommand cmd) {
        if (!featureProperties.allowUserCategories()){
            throw new AuthorizationException("Category creation is disabled");
        }
        User creator = userRepository.findById(cmd.userId()).orElseThrow(
                () -> new EntityNotFoundException("User", cmd.userId())
        );
        if (categoryRepository.existsByNameAndUserId(cmd.name(), cmd.userId())) {
            throw new DuplicateEntityException("Category", "name", cmd.name());
        }
        Category category = Category.builder()
                .name(cmd.name())
                .description(cmd.description())
                .icon(cmd.icon())
                .user(creator)
                .build();

        return CategoryDetailsResult.fromEntity(categoryRepository.save(category));
    }

    @Override
    public CategoryDetailsResult getCategoryById(Long categoryId, Long userId) {
        Category category = loadCategoryAuthorized(categoryId, userId);
        return CategoryDetailsResult.fromEntity(category);
    }

    @Override
    public void updateCategory(CategoryUpdateCommand cmd) {
        Category category = loadCategoryAuthorized(cmd.categoryId(), cmd.userId());
        if (!cmd.name().equals(category.getName()) &&
                categoryRepository.existsByNameAndUserId(cmd.name(), cmd.userId())) {
            throw new DuplicateEntityException("Category", "name", cmd.name());
        }
        category.setName(cmd.name());
        category.setDescription(cmd.description());
        category.setIcon(cmd.icon());
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId, Long userId) {
        Category category = loadCategoryAuthorized(categoryId, userId);
        categoryRepository.delete(category);
    }

    private Category loadCategoryAuthorized(Long categoryId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User", userId)
        );
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException("Category", categoryId)
        );
        if (category.isDefault()){
            if (user.getRole() != Role.ADMIN) {
                throw new AuthorizationException("You cannot modify default category");
            }
        }
        if (!category.isDefault()){
            if (!category.getUser().getId().equals(userId) &&
                    user.getRole() != Role.ADMIN &&
                    user.getRole() != Role.MODERATOR) {
                throw new AuthorizationException("You are not authorized to modify this category");
            }
        }
        return category;
    }
}
