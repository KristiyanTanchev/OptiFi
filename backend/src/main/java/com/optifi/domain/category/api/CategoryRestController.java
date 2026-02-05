package com.optifi.domain.category.api;

import com.optifi.config.web.CurrentUser;
import com.optifi.domain.category.api.mapper.CategoryMapper;
import com.optifi.domain.category.api.request.CategoryCreateRequestDto;
import com.optifi.domain.category.api.request.CategoryUpdateRequestDto;
import com.optifi.domain.category.api.response.CategoryDetailsResponseDto;
import com.optifi.domain.category.api.response.CategorySummaryResponseDto;
import com.optifi.domain.category.application.CategoryService;
import com.optifi.domain.category.application.command.CategoryCreateCommand;
import com.optifi.domain.category.application.command.CategoryUpdateCommand;
import com.optifi.domain.category.application.result.CategoryDetailsResult;
import com.optifi.domain.category.application.result.CategorySummaryResult;
import com.optifi.domain.shared.UserContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@PreAuthorize("isAuthenticated()")
public class CategoryRestController {

    private final CategoryService categoryService;
    private final CategoryMapper mapper;

    @GetMapping

    public ResponseEntity<List<CategorySummaryResponseDto>> getUserOwnCategories(
            @CurrentUser UserContext ctx
    ) {
        List<CategorySummaryResult> categories = categoryService.getUsersCategories(ctx.userId());
        List<CategorySummaryResponseDto> dto = categories.stream().map(mapper::toSummaryDto).toList();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<CategoryDetailsResponseDto> createCategory(
            @Valid @RequestBody CategoryCreateRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        CategoryCreateCommand cmd = mapper.toCreateCommand(dto, ctx);
        CategoryDetailsResult result = categoryService.createCategory(cmd);
        CategoryDetailsResponseDto response = mapper.toDetailsDto(result, ctx);
        return ResponseEntity
                .created(URI.create("/api/categories/" + response.id()))
                .body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDetailsResponseDto> getCategoryById(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        CategoryDetailsResult result = categoryService.getCategoryById(id, ctx.userId());
        CategoryDetailsResponseDto response = mapper.toDetailsDto(result, ctx);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody CategoryUpdateRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        CategoryUpdateCommand cmd = mapper.toUpdateCommand(id, dto, ctx);
        categoryService.updateCategory(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        categoryService.deleteCategory(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }
}
