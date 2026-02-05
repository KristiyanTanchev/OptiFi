package com.optifi.config.web.converters;

import com.optifi.domain.shared.TimeBucket;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TimeBucketConverter implements Converter<String, TimeBucket> {

    @Override
    public TimeBucket convert(String source) {
        return TimeBucket.from(source);
    }
}
