package com.optifi.domain.transaction.api.mapper;

import com.optifi.domain.category.api.mapper.CategoryMapper;
import com.optifi.domain.transaction.api.response.TransactionDetailsResponseDto;
import com.optifi.domain.transaction.application.result.TransactionDetailsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final CategoryMapper categoryMapper;

    public TransactionDetailsResponseDto toDetailsResponseDto(TransactionDetailsResult result) {
        return TransactionDetailsResponseDto.builder()
                .id(result.id())
                .amount(result.amount())
                .description(result.description())
                .accountId(result.accountId())
                .category(categoryMapper.toSummaryDto(result.category()))
                .occurredAt(result.occurredAt())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .build();
    }
}
