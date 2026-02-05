package com.optifi.config.web.converters;

import com.optifi.domain.shared.AccountType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountTypeConverter implements Converter<String, AccountType> {

    @Override
    public AccountType convert(String source) {
        return AccountType.from(source);
    }
}
