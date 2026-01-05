package com.optifi.domain.account.api;

import com.optifi.domain.account.api.request.AccountCreateRequestDto;
import com.optifi.domain.account.api.response.AccountDetailsResposneDto;
import com.optifi.domain.account.api.response.AccountSummaryResponseDto;
import com.optifi.domain.account.application.AccountService;
import com.optifi.domain.account.application.command.CreateAccountCommand;
import com.optifi.domain.account.application.result.AccountDetailsResult;
import com.optifi.domain.account.application.result.AccountSummaryResult;
import com.optifi.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
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
    public ResponseEntity<AccountDetailsResposneDto> createAccount(
            @Valid @RequestBody AccountCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CreateAccountCommand cmd = CreateAccountCommand.from(
                userDetails.getId(),
                dto.name(),
                dto.type(),
                dto.currency(),
                dto.institution()
        );
        AccountDetailsResult result = accountService.createAccount(cmd);
        AccountDetailsResposneDto responseDto = AccountDetailsResposneDto.fromResult(result);
        return ResponseEntity.ok(responseDto);
    }
}
