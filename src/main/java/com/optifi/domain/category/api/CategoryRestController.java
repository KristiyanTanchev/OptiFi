package com.optifi.domain.category.api;

import com.optifi.domain.category.api.request.CategoryCreateRequestDto;
import com.optifi.domain.category.api.request.CategoryUpdateRequestDto;
import com.optifi.domain.category.api.response.CategoryDetailsResponseDto;
import com.optifi.domain.category.api.response.CategorySummaryResponseDto;
import com.optifi.domain.category.application.CategoryService;
import com.optifi.domain.category.application.command.CategoryCreateCommand;
import com.optifi.domain.category.application.command.CategoryUpdateCommand;
import com.optifi.domain.category.application.result.CategoryDetailsResult;
import com.optifi.domain.category.application.result.CategorySummaryResult;
import com.optifi.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategorySummaryResponseDto>> getUserOwnCategories(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CategorySummaryResult> categories = categoryService.getUsersCategories(userDetails.getId());
        List<CategorySummaryResponseDto> dto = categories.stream().map(CategorySummaryResponseDto::fromResult).toList();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryDetailsResponseDto> createCategory(
            @Valid @RequestBody CategoryCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CategoryCreateCommand cmd = dto.toCreateCommand(userDetails.getId());
        CategoryDetailsResult result = categoryService.createCategory(cmd);
        CategoryDetailsResponseDto response = CategoryDetailsResponseDto.fromResult(result);
        return ResponseEntity
                .created(URI.create("/api/categories/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryDetailsResponseDto> getCategoryById(
            @PathVariable @NotNull @Positive Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CategoryDetailsResult result = categoryService.getCategoryById(id, userDetails.getId());
        CategoryDetailsResponseDto response = CategoryDetailsResponseDto.fromResult(result);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateCategory(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody CategoryUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CategoryUpdateCommand cmd = dto.toUpdateCommand(userDetails.getId(), id);
        categoryService.updateCategory(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable @NotNull @Positive Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        categoryService.deleteCategory(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
