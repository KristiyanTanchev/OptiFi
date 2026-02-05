package com.optifi.domain.transaction.api;

import com.optifi.config.web.CurrentUser;
import com.optifi.domain.shared.UserContext;
import com.optifi.domain.transaction.api.mapper.TransactionMapper;
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
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final TransactionMapper mapper;

    @GetMapping
    public ResponseEntity<Page<TransactionSummaryResponseDto>> getAllTransactions(
            @PathVariable @NotNull @Positive Long accountId,
            @Valid @ModelAttribute GetUserTransactionsRequestDto requestDto,
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable,
            @CurrentUser UserContext ctx
    ) {
        TransactionQuery query = mapper.toTransactionQuery(accountId, requestDto, ctx);
        Page<TransactionSummaryResult> result = transactionService.getAllUserTransactions(query, pageable);
        Page<TransactionSummaryResponseDto> response = result
                .map((transaction) -> mapper.toSummaryDto(transaction, ctx));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDetailsResponseDto> getTransaction(
            @PathVariable @NotNull @Positive Long accountId,
            @PathVariable @NotNull @Positive Long transactionId,
            @CurrentUser UserContext ctx
    ) {
        TransactionReferenceCommand cmd = mapper.toReferenceCommand(accountId, transactionId, ctx);
        TransactionDetailsResult result = transactionService.getTransaction(cmd);
        TransactionDetailsResponseDto responseDto = mapper.toDetailsDto(result, ctx);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<TransactionDetailsResponseDto> createTransaction(
            @PathVariable @NotNull @Positive Long accountId,
            @RequestBody @Valid TransactionCreateRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        TransactionCreateCommand cmd = mapper.toCreateCommand(accountId, dto, ctx);
        TransactionDetailsResult result = transactionService.createTransaction(cmd);
        TransactionDetailsResponseDto responseDto = mapper.toDetailsDto(result, ctx);
        return ResponseEntity
                .created(URI.create("/api/accounts/" + accountId + "/transactions/" + responseDto.id()))
                .body(responseDto);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Void> updateTransaction(
            @PathVariable @NotNull @Positive Long accountId,
            @PathVariable @NotNull @Positive Long transactionId,
            @RequestBody @Valid TransactionUpdateRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        TransactionUpdateCommand cmd = mapper.toUpdateCommand(accountId, transactionId, dto, ctx);
        transactionService.updateTransaction(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable @NotNull @Positive Long accountId,
            @PathVariable @NotNull @Positive Long transactionId,
            @CurrentUser UserContext ctx
    ) {
        TransactionReferenceCommand cmd = mapper.toReferenceCommand(accountId, transactionId, ctx);
        transactionService.deleteTransaction(cmd);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<TransactionGetSummaryResponseDto> getTransactionsSummary(
            @PathVariable @NotNull @Positive Long accountId,
            @Valid @ModelAttribute TransactionGetSummaryRequestDto dto,
            @CurrentUser UserContext ctx
    ) {
        TransactionGetSummaryCommand cmd = mapper.toGetSummaryCommand(accountId, dto, ctx);
        TransactionGetSummaryResult result = transactionService.getTransactionSummary(cmd);
        TransactionGetSummaryResponseDto response = mapper.toGetSummaryDto(result, ctx);
        return ResponseEntity.ok(response);
    }
}
