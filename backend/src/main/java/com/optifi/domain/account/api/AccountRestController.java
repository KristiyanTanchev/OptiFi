package com.optifi.domain.account.api;

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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AccountRestController {
    private final AccountService accountService;
    private final AccountMapper mapper;

    @GetMapping
    public ResponseEntity<List<AccountSummaryResponseDto>> getAllOwnAccounts(
            @CurrentUser UserContext ctx
    ) {
        List<AccountSummaryResult> accounts = accountService.getAllUserAccounts(ctx.userId());
        List<AccountSummaryResponseDto> result = accounts.stream().map(mapper::toSummaryDto).toList();
        return ResponseEntity.ok(result);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<AccountDetailsResponseDto> getAccountById(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        AccountDetailsResult result = accountService.getAccountById(id, ctx.userId());
        AccountDetailsResponseDto responseDto = mapper.toDetailsDto(result, ctx);
        return ResponseEntity.ok(responseDto);
    }

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

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveAccount(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        accountService.archiveAccount(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveAccount(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        accountService.unarchiveAccount(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable @NotNull @Positive Long id,
            @CurrentUser UserContext ctx
    ) {
        accountService.deleteAccount(id, ctx.userId());
        return ResponseEntity.noContent().build();
    }
}
