package com.optifi.config.web.converters;

import com.optifi.domain.shared.BudgetPeriod;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class budgetPeriodConverter implements Converter<String, BudgetPeriod> {

    @Override
    public BudgetPeriod convert(String source) {
        return BudgetPeriod.from(source);
    }
}
