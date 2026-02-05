package com.optifi.config.web;

import com.optifi.domain.shared.UserContext;
import com.optifi.exceptions.AuthorizationException;
import com.optifi.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final TimezoneHeaderResolver timezoneHeaderResolver;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && UserContext.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new AuthorizationException("No authenticated user found");
        }

        String headerTz = webRequest.getHeader("X-Timezone");

        ZoneId fallback = principal.getZoneId() != null ? principal.getZoneId() : ZoneId.of("Europe/Sofia");
        ZoneId zoneId = timezoneHeaderResolver.resolve(headerTz, fallback);

        return UserContext.builder()
                .userId(principal.getId())
                .zoneId(zoneId)
                .currency(principal.getCurrency())
                .build();
    }
}
