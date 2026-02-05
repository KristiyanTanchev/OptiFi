package com.optifi.config.web.converters;

import com.optifi.domain.shared.TransactionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionTypeConverter implements Converter<String, TransactionType> {

    @Override
    public TransactionType convert(String source) {
        if (source == null) return null;
        return TransactionType.from(source.trim());
    }
}


