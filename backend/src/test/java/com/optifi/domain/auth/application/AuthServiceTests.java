package com.optifi.domain.auth.application;

import com.optifi.domain.auth.application.command.LoginCommand;
import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.domain.auth.application.result.LoginResult;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.user.application.UserService;
import com.optifi.domain.shared.Role;
import com.optifi.security.CustomUserDetails;
import com.optifi.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private CustomUserDetails userDetails;
    private LoginCommand loginCommand;

    @BeforeEach
    void setUp() {
        loginCommand = new LoginCommand("john", "pass");
        Authentication authentication = mock(Authentication.class);
        userDetails = new CustomUserDetails(
                "username",
                "password-encoded",
                true,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                1,
                Role.ADMIN,
                Currency.USD,
                ZoneId.of("Europe/Sofia")
        );

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any())).thenReturn("token");
    }

    @Test
    void login_Should_succeed_When_validCredentials() {
        LoginResult result = authService.login(loginCommand);

        assertEquals(userDetails.getId(), result.id());
        assertEquals(userDetails.getUsername(), result.username());
        assertEquals(userDetails.getRole(), result.role());
        assertEquals("token", result.token());
    }

    @Test
    void register_Should_callCreateUser() {
        RegisterUserCommand cmd = new RegisterUserCommand("john", "pass", "email");
        LoginResult result = authService.register(cmd);
        verify(userService, times(1)).createUser(cmd, Role.USER);

        assertEquals(userDetails.getId(), result.id());
        assertEquals(userDetails.getUsername(), result.username());
        assertEquals(userDetails.getRole(), result.role());
        assertEquals("token", result.token());
    }
}
