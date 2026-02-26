package com.optifi.domain.category.application;

import com.optifi.config.FeatureProperties;
import com.optifi.domain.category.application.command.CategoryCreateCommand;
import com.optifi.domain.category.application.command.CategoryUpdateCommand;
import com.optifi.domain.category.application.result.CategoryDetailsResult;
import com.optifi.domain.category.application.result.CategorySummaryResult;
import com.optifi.domain.category.model.Category;
import com.optifi.domain.category.repository.CategoryRepository;
import com.optifi.domain.shared.Role;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.exceptions.AuthorizationException;
import com.optifi.exceptions.DuplicateEntityException;
import com.optifi.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    @Mock
    CategoryRepository categoryRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    FeatureProperties featureProperties;

    @InjectMocks
    CategoryServiceImpl categoryService;

    // ---- getUsersCategories ----

    @Test
    void getUsersCategories_Should_throw_When_userDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.getUsersCategories(1L));

        verify(categoryRepository, never()).findAllByUserId(anyLong());
    }

    @Test
    void getUsersCategories_Should_returnMappedResults_When_userExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        Category c1 = Category.builder().id(10L).name("Food").build();
        Category c2 = Category.builder().id(11L).name("Bills").build();
        when(categoryRepository.findAllByUserId(1L)).thenReturn(List.of(c1, c2));

        List<CategorySummaryResult> result = categoryService.getUsersCategories(1L);

        assertEquals(2, result.size());
        verify(categoryRepository).findAllByUserId(1L);
    }

    // ---- createCategory ----

    @Test
    void createCategory_Should_throwForbidden_When_featureDisabled() {
        when(featureProperties.allowUserCategories()).thenReturn(false);

        CategoryCreateCommand cmd = new CategoryCreateCommand(1L, "Food", "desc", "icon");

        assertThrows(AuthorizationException.class, () -> categoryService.createCategory(cmd));

        verifyNoInteractions(userRepository, categoryRepository);
    }

    @Test
    void createCategory_Should_throwNotFound_When_creatorMissing() {
        when(featureProperties.allowUserCategories()).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CategoryCreateCommand cmd = new CategoryCreateCommand(1L, "Food", "desc", "icon");

        assertThrows(EntityNotFoundException.class, () -> categoryService.createCategory(cmd));

        verify(categoryRepository, never()).existsByNameAndUserId(anyString(), anyLong());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void createCategory_Should_throwDuplicate_When_nameExistsForUser() {
        when(featureProperties.allowUserCategories()).thenReturn(true);

        User creator = User.builder().id(1L).role(Role.USER).username("u").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        when(categoryRepository.existsByNameAndUserId("Food", 1L)).thenReturn(true);

        CategoryCreateCommand cmd = new CategoryCreateCommand(1L, "Food", "desc", "icon");

        assertThrows(DuplicateEntityException.class, () -> categoryService.createCategory(cmd));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void createCategory_Should_saveAndReturnResult_When_valid() {
        when(featureProperties.allowUserCategories()).thenReturn(true);

        User creator = User.builder().id(1L).role(Role.USER).username("u").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        when(categoryRepository.existsByNameAndUserId("Food", 1L)).thenReturn(false);

        // save returns entity with id
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        CategoryCreateCommand cmd = new CategoryCreateCommand(1L, "Food", "desc", "icon");

        CategoryDetailsResult result = categoryService.createCategory(cmd);

        assertNotNull(result);
        verify(categoryRepository).save(argThat(c ->
                "Food".equals(c.getName()) &&
                        "desc".equals(c.getDescription()) &&
                        "icon".equals(c.getIcon()) &&
                        c.getUser() != null &&
                        c.getUser().getId().equals(1L)
        ));
    }

    // ---- getCategoryById ----

    @Test
    void getCategoryById_Should_throw_When_userMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(10L, 1L));

        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    void getCategoryById_Should_throw_When_categoryMissing() {
        User user = User.builder().id(1L).role(Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(10L, 1L));
    }

    @Test
    void getCategoryById_Should_throwForbidden_When_defaultCategoryAndNotAdmin() {
        User user = User.builder().id(1L).role(Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Category cat = mock(Category.class);
        when(cat.isDefault()).thenReturn(true);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(cat));

        assertThrows(AuthorizationException.class, () -> categoryService.getCategoryById(10L, 1L));
    }

    @Test
    void getCategoryById_Should_allowDefault_When_admin() {
        User admin = User.builder().id(1L).role(Role.ADMIN).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        Category cat = mock(Category.class);
        when(cat.isDefault()).thenReturn(true);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(cat));

        assertDoesNotThrow(() -> categoryService.getCategoryById(10L, 1L));
    }

    @Test
    void getCategoryById_Should_throwForbidden_When_nonDefaultNotOwnedAndNotPrivileged() {
        User user = User.builder().id(1L).role(Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User owner = User.builder().id(2L).role(Role.USER).build();
        Category cat = Category.builder().id(10L).user(owner).build();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(cat));

        assertThrows(AuthorizationException.class, () -> categoryService.getCategoryById(10L, 1L));
    }

    @Test
    void getCategoryById_Should_allow_When_owner() {
        User user = User.builder().id(1L).role(Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User owner = User.builder().id(1L).role(Role.USER).build();
        Category cat = Category.builder().id(10L).user(owner).build();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(cat));

        assertDoesNotThrow(() -> categoryService.getCategoryById(10L, 1L));
    }

    @Test
    void getCategoryById_Should_allow_When_moderator() {
        User moderator = User.builder().id(1L).role(Role.MODERATOR).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(moderator));

        User owner = User.builder().id(2L).role(Role.USER).build();
        Category cat = Category.builder().id(10L).user(owner).build();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(cat));

        assertDoesNotThrow(() -> categoryService.getCategoryById(10L, 1L));
    }

    @Test
    void getCategoryById_Should_allow_When_admin() {
        User moderator = User.builder().id(1L).role(Role.ADMIN).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(moderator));

        User owner = User.builder().id(2L).role(Role.USER).build();
        Category cat = Category.builder().id(10L).user(owner).build();

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(cat));

        assertDoesNotThrow(() -> categoryService.getCategoryById(10L, 1L));
    }

    // ---- updateCategory ----

    @Test
    void updateCategory_Should_throwDuplicate_When_renamingToExistingName() {
        User user = User.builder().id(1L).role(Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User owner = User.builder().id(1L).role(Role.USER).build();
        Category existing = Category.builder().id(10L).name("Old").user(owner).build();
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(existing));

        when(categoryRepository.existsByNameAndUserId("New", 1L)).thenReturn(true);

        CategoryUpdateCommand cmd = new CategoryUpdateCommand(1L, 10L, "New", "d", "i");

        assertThrows(DuplicateEntityException.class, () -> categoryService.updateCategory(cmd));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_Should_save_When_nameChanged() {
        User user = User.builder().id(1L).role(Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User owner = User.builder().id(1L).role(Role.USER).build();
        Category existing = Category.builder().id(10L).name("Same").user(owner).build();
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(existing));

        CategoryUpdateCommand cmd = new CategoryUpdateCommand(1L, 10L, "New", "newDesc", "newIcon");

        categoryService.updateCategory(cmd);

        verify(categoryRepository).save(argThat(c ->
                "New".equals(c.getName()) &&
                        "newDesc".equals(c.getDescription()) &&
                        "newIcon".equals(c.getIcon())
        ));
    }

    @Test
    void updateCategory_Should_save_When_nameUnchanged() {
        User user = User.builder().id(1L).role(Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User owner = User.builder().id(1L).role(Role.USER).build();
        Category existing = Category.builder().id(10L).name("Same").user(owner).build();
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(existing));

        CategoryUpdateCommand cmd = new CategoryUpdateCommand(1L, 10L, "Same", "newDesc", "newIcon");

        categoryService.updateCategory(cmd);

        verify(categoryRepository).save(argThat(c ->
                "Same".equals(c.getName()) &&
                        "newDesc".equals(c.getDescription()) &&
                        "newIcon".equals(c.getIcon())
        ));
    }

    @Test
    void deleteCategory_Should_delete_When_authorized() {
        User admin = User.builder().id(1L).role(Role.ADMIN).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        // default category allowed for admin
        Category cat = mock(Category.class);
        when(cat.isDefault()).thenReturn(true);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(cat));

        categoryService.deleteCategory(10L, 1L);

        verify(categoryRepository).delete(cat);
    }
}