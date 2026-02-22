package com.optifi.domain.account.api;

import com.optifi.config.openApi.ApiForbidden;
import com.optifi.config.openApi.ApiNotFound;
import com.optifi.config.openApi.ApiValidationError;
import com.optifi.config.web.CurrentUser;
import com.optifi.domain.account.api.mapper.AccountMapper;
import com.optifi.domain.account.api.request.AccountCreateRequestDto;
import com.optifi.domain.account.api.request.AccountUpdateRequestDto;
import com.optifi.domain.account.api.response.AccountDetailsResponseDto;
import com.optifi.domain.account.api.response.AccountSummaryResponseDto;
import com.optifi.domain.account.application.AccountService;
import com.optifi.domain.account.application.command.AccountUpdateCommand;
import com.optifi.domain.account.application.command.AccountCreateCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.domain.shared.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Accounts")
@ApiForbidden

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AccountRestController {
    private final AccountService accountService;
    private final AccountMapper mapper;

    @Operation(summary = "List my accounts")
    @ApiResponse(responseCode = "200", description = "Accounts returned")
    @GetMapping
    public ResponseEntity<List<AccountSummaryResponseDto>> getAllOwnAccounts(
            @CurrentUser UserContext ctx
    ) {
        List<AccountSummaryResult> accounts = accountService.getAllUserAccounts(ctx.userId());
        List<AccountSummaryResponseDto> result = accounts.stream().map(mapper::toSummaryDto).toList();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Create account")
    @ApiResponse(responseCode = "201", description = "Account created")
    @ApiValidationError
    @PostMapping
    public ResponseEntity<AccountDetailsResponseDto> createAccount(
            @Valid @RequestBody AccountCreateRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        AccountCreateCommand cmd = mapper.toCreateCommand(dto, ctx);
        AccountDetailsResult result = accountService.createAccount(cmd);
        AccountDetailsResponseDto responseDto = mapper.toDetailsDto(result, ctx);
        return ResponseEntity.created(URI.create("/api/accounts/" + responseDto.id())).body(responseDto);
    }

    @Operation(summary = "Get account by id")
    @ApiResponse(responseCode = "200", description = "Account returned")
    @ApiValidationError
    @ApiNotFound
    @GetMapping("/{id}")
    public ResponseEntity<AccountDetailsResponseDto> getAccountById(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        AccountDetailsResult result = accountService.getAccountById(id, ctx.userId());
        AccountDetailsResponseDto responseDto = mapper.toDetailsDto(result, ctx);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Update account")
    @ApiResponse(responseCode = "204", description = "Account updated")
    @ApiValidationError
    @ApiNotFound
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAccount(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody AccountUpdateRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        AccountUpdateCommand cmd = mapper.toUpdateCommand(id, dto, ctx);
        accountService.updateAccount(cmd);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Archive account")
    @ApiResponse(responseCode = "204", description = "Account archived")
    @ApiValidationError(description = "Only positive id accepted")
    @ApiNotFound(description = "Account not found")
    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveAccount(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        accountService.archiveAccount(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Unarchive account")
    @ApiResponse(responseCode = "204", description = "Account unarchived")
    @ApiValidationError
    @ApiNotFound
    @PutMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveAccount(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        accountService.unarchiveAccount(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete account")
    @ApiResponse(responseCode = "204", description = "Account deleted")
    @ApiValidationError
    @ApiNotFound
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        accountService.deleteAccount(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }
}
