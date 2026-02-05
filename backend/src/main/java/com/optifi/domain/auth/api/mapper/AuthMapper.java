package com.optifi.domain.auth.api.mapper;

import com.optifi.domain.auth.api.response.LoginResponseDto;
import com.optifi.domain.auth.application.result.LoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMapper {
    private static final String TOKEN_TYPE = "Bearer";

    public LoginResponseDto toLoginResponseDto(LoginResult result) {
        return LoginResponseDto.builder()
                .token(result.token())
                .role(result.role())
                .type(TOKEN_TYPE)
                .id(result.id())
                .username(result.username())
                .build();
    }
}
