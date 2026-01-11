package com.optifi.domain.transaction.api;

import com.optifi.domain.transaction.api.request.GetUserTransactionsRequestDto;
import com.optifi.domain.transaction.api.request.TransactionCreateRequestDto;
import com.optifi.domain.transaction.api.request.TransactionUpdateRequestDto;
import com.optifi.domain.transaction.api.response.TransactionDetailsResponseDto;
import com.optifi.domain.transaction.api.response.TransactionSummaryResponseDto;
import com.optifi.domain.transaction.application.TransactionService;
import com.optifi.domain.transaction.application.command.TransactionCreateCommand;
import com.optifi.domain.transaction.application.command.TransactionQuery;
import com.optifi.domain.transaction.application.command.TransactionUpdateCommand;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import com.optifi.domain.transaction.application.result.TransactionSummaryResult;
import com.optifi.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionSummaryResponseDto>> getAllTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute GetUserTransactionsRequestDto requestDto,
            Pageable pageable
    ) {
        TransactionQuery query = requestDto.toQuery(userDetails.getId());
        Page<TransactionSummaryResult> result = transactionService.getAllUserTransactions(query, pageable);
        Page<TransactionSummaryResponseDto> response = result.map(TransactionSummaryResponseDto::fromResult);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDetailsResponseDto> getTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionDetailsResult result = transactionService.getTransaction(id, userDetails.getId());
        TransactionDetailsResponseDto responseDto = TransactionDetailsResponseDto.fromResult(result);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<TransactionDetailsResponseDto> createTransaction(
            @RequestBody @Valid TransactionCreateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionCreateCommand cmd = dto.toCreateCommand(userDetails.getId());
        TransactionDetailsResult result = transactionService.createTransaction(cmd);
        TransactionDetailsResponseDto responseDto = TransactionDetailsResponseDto.fromResult(result);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTransaction(
            @PathVariable Long id,
            @RequestBody @Valid TransactionUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionUpdateCommand cmd = dto.toUpdateCommand(id, userDetails.getId());
        transactionService.updateTransaction(cmd);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        transactionService.deleteTransaction(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
