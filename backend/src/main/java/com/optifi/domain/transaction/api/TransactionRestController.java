package com.optifi.domain.transaction.api;

import com.optifi.domain.transaction.api.request.GetUserTransactionsRequestDto;
import com.optifi.domain.transaction.api.request.TransactionCreateRequestDto;
import com.optifi.domain.transaction.api.request.TransactionUpdateRequestDto;
import com.optifi.domain.transaction.api.request.TransactionGetSummaryRequestDto;
import com.optifi.domain.transaction.api.response.TransactionDetailsResponseDto;
import com.optifi.domain.transaction.api.response.TransactionSummaryResponseDto;
import com.optifi.domain.transaction.api.response.TransactionGetSummaryResponseDto;
import com.optifi.domain.transaction.application.TransactionService;
import com.optifi.domain.transaction.application.command.*;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionGetSummaryResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import com.optifi.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TransactionRestController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionSummaryResponseDto>> getAllTransactions(
            @PathVariable @NotNull @Positive Long accountId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute GetUserTransactionsRequestDto requestDto,
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        TransactionQuery query = requestDto.toQuery(userDetails.getId(), accountId);
        Page<TransactionSummaryResult> result = transactionService.getAllUserTransactions(query, pageable);
        Page<TransactionSummaryResponseDto> response = result.map(TransactionSummaryResponseDto::fromResult);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDetailsResponseDto> getTransaction(
            @PathVariable @NotNull @Positive Long accountId,
            @PathVariable @NotNull @Positive Long transactionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(userDetails.getId(), accountId, transactionId);
        TransactionDetailsResult result = transactionService.getTransaction(cmd);
        TransactionDetailsResponseDto responseDto = TransactionDetailsResponseDto.fromResult(result);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<TransactionDetailsResponseDto> createTransaction(
            @PathVariable @NotNull @Positive Long accountId,
            @RequestBody @Valid TransactionCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionCreateCommand cmd = dto.toCreateCommand(userDetails.getId(), accountId);
        TransactionDetailsResult result = transactionService.createTransaction(cmd);
        TransactionDetailsResponseDto responseDto = TransactionDetailsResponseDto.fromResult(result);
        return ResponseEntity
                .created(URI.create("/api/accounts/" + accountId + "/transactions/" + responseDto.id()))
                .body(responseDto);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Void> updateTransaction(
            @PathVariable @NotNull @Positive Long accountId,
            @PathVariable @NotNull @Positive Long transactionId,
            @RequestBody @Valid TransactionUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionUpdateCommand cmd = dto.toUpdateCommand(accountId, transactionId, userDetails.getId());
        transactionService.updateTransaction(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable @NotNull @Positive Long accountId,
            @PathVariable @NotNull @Positive Long transactionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionReferenceCommand cmd = new TransactionReferenceCommand(userDetails.getId(), accountId, transactionId);
        transactionService.deleteTransaction(cmd);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<TransactionGetSummaryResponseDto> getTransactionsSummary(
            @PathVariable @NotNull @Positive Long accountId,
            @Valid @ModelAttribute TransactionGetSummaryRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionGetSummaryCommand cmd = dto.toCommand(userDetails.getId(), accountId);
        TransactionGetSummaryResult result = transactionService.getTransactionSummary(cmd);
        TransactionGetSummaryResponseDto response = TransactionGetSummaryResponseDto.from(result);
        return ResponseEntity.ok(response);
    }
}
