package com.optifi.domain.account.api;

import com.optifi.domain.account.api.request.AccountCreateRequestDto;
import com.optifi.domain.account.api.request.AccountUpdateRequestDto;
import com.optifi.domain.account.api.response.AccountDetailsResponseDto;
import com.optifi.domain.account.api.response.AccountSummaryResponseDto;
import com.optifi.domain.account.application.AccountService;
import com.optifi.domain.account.application.command.AccountUpdateCommand;
import com.optifi.domain.account.application.command.CreateAccountCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.application.result.AccountSummaryResult;
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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AccountRestController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountSummaryResponseDto>> getAllOwnAccounts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<AccountSummaryResult> accounts = accountService.getAllUserAccounts(userDetails.getId());
        List<AccountSummaryResponseDto> result = accounts.stream().map(AccountSummaryResponseDto::fromResult).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<AccountDetailsResponseDto> createAccount(
            @Valid @RequestBody AccountCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CreateAccountCommand cmd = dto.toCreateCommand(userDetails.getId());
        AccountDetailsResult result = accountService.createAccount(cmd);
        AccountDetailsResponseDto responseDto = AccountDetailsResponseDto.fromResult(result);
        return ResponseEntity.created(URI.create("/api/accounts/" + responseDto.id())).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDetailsResponseDto> getAccountById(
            @PathVariable @NotNull @Positive Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AccountDetailsResult result = accountService.getAccountById(id, userDetails.getId());
        AccountDetailsResponseDto responseDto = AccountDetailsResponseDto.fromResult(result);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAccount(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody AccountUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AccountUpdateCommand cmd = dto.toUpdateCommand(id, userDetails.getId());
        accountService.updateAccount(cmd);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archiveAccount(
            @PathVariable @NotNull @Positive Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        accountService.archiveAccount(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveAccount(
            @PathVariable @NotNull @Positive Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        accountService.unarchiveAccount(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable @NotNull @Positive Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        accountService.deleteAccount(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
