package com.optifi.domain.budget.api;

import com.optifi.config.web.CurrentUser;
import com.optifi.domain.budget.api.mapper.BudgetMapper;
import com.optifi.domain.budget.api.request.BudgetEvaluationRequestDto;
import com.optifi.domain.budget.api.request.BudgetSearchRequestDto;
import com.optifi.domain.budget.api.request.BudgetCreateRequestDto;
import com.optifi.domain.budget.api.request.BudgetUpdateRequestDto;
import com.optifi.domain.budget.api.response.BudgetDetailsResponseDto;
import com.optifi.domain.budget.api.response.BudgetEvaluationResponseDto;
import com.optifi.domain.budget.application.BudgetService;
import com.optifi.domain.budget.application.command.BudgetCreateCommand;
import com.optifi.domain.budget.application.command.BudgetEvaluationCommand;
import com.optifi.domain.budget.application.command.BudgetQuery;
import com.optifi.domain.budget.application.command.BudgetUpdateCommand;
import com.optifi.domain.budget.application.result.BudgetDetailsResult;
import com.optifi.domain.budget.application.result.BudgetEvaluationResult;
import com.optifi.domain.shared.UserContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/budgets")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class BudgetRestController {

    private final BudgetService budgetService;
    private final BudgetMapper mapper;

    @GetMapping
    public ResponseEntity<Page<BudgetDetailsResponseDto>> getBudgets(
            @Valid @ModelAttribute BudgetSearchRequestDto dto,
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable,
            @CurrentUser UserContext ctx
    ) {
        BudgetQuery query = mapper.toQuery(dto, ctx);
        Page<BudgetDetailsResult> result = budgetService.getBudgetsForUser(query, pageable);
        return ResponseEntity.ok(result.map((b) -> mapper.toDetailsDto(b, ctx)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDetailsResponseDto> getBudgetById(
            @PathVariable @Positive(message = "id must be positive") Long id,
            @CurrentUser UserContext ctx
    ) {
        BudgetDetailsResult result = budgetService.getBudgetById(id, ctx.userId());
        BudgetDetailsResponseDto response = mapper.toDetailsDto(result, ctx);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BudgetDetailsResponseDto> createBudget(
            @RequestBody @Valid BudgetCreateRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        BudgetCreateCommand cmd = mapper.toCreateCommand(dto, ctx);
        BudgetDetailsResult result = budgetService.createBudget(cmd);
        BudgetDetailsResponseDto response = mapper.toDetailsDto(result, ctx);
        return ResponseEntity.created(URI.create("/api/budgets" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBudget(
            @PathVariable Long id,
            @RequestBody @Valid BudgetUpdateRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        BudgetUpdateCommand cmd = mapper.toUpdateCommand(id, dto, ctx);
        budgetService.updateBudget(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveBudget(
            @PathVariable @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        budgetService.archiveBudget(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveBudget(
            @PathVariable @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        budgetService.unarchiveBudget(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        budgetService.deleteBudget(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/evaluate")
    public ResponseEntity<BudgetEvaluationResponseDto> getBudgetsEvaluation(
            @Valid @ModelAttribute BudgetEvaluationRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        BudgetEvaluationCommand cmd = mapper.toEvaluationCommand(dto, ctx);
        BudgetEvaluationResult result = budgetService.evaluateBudget(cmd);
        BudgetEvaluationResponseDto response = mapper.toEvaluationDto(result);
        return ResponseEntity.ok(response);
    }
}
