package com.optifi.domain.auth.application;

import com.optifi.domain.auth.application.command.GoogleOidcLoginCommand;
import com.optifi.domain.auth.application.command.LoginCommand;
import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.domain.auth.application.result.LoginResult;
import com.optifi.domain.shared.Currency;
import com.optifi.domain.user.application.UserService;
import com.optifi.domain.shared.Role;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private JwtDecoder googleJwtDecoder;

    @Mock
    private UserRepository userRepository;

    private final Authentication authentication = mock(Authentication.class);

    @InjectMocks
    private AuthServiceImpl authService;

    private CustomUserDetails userDetails;
    private LoginCommand loginCommand;

    @BeforeEach
    void setUp() {
        loginCommand = new LoginCommand("john", "pass");
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
    }

    @Test
    void login_Should_succeed_When_validCredentials() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any())).thenReturn("token");
        LoginResult result = authService.login(loginCommand);

        assertEquals(userDetails.getId(), result.id());
        assertEquals(userDetails.getUsername(), result.username());
        assertEquals(userDetails.getRole(), result.role());
        assertEquals("token", result.token());
    }

    @Test
    void register_Should_callCreateUser() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any())).thenReturn("token");
        RegisterUserCommand cmd = new RegisterUserCommand("john", "pass", "email");
        LoginResult result = authService.register(cmd);
        verify(userService, times(1)).createUser(cmd, Role.USER);

        assertEquals(userDetails.getId(), result.id());
        assertEquals(userDetails.getUsername(), result.username());
        assertEquals(userDetails.getRole(), result.role());
        assertEquals("token", result.token());
    }

    @Test
    void loginWithGoogle_existingUser_doesNotCreateUser() {
        // arrange
        String idToken = "token";
        String sub = "google-sub";
        String email = "a@b.com";

        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(sub);
        when(jwt.getClaimAsString("email")).thenReturn(email);
        when(googleJwtDecoder.decode(anyString())).thenReturn(jwt);
        when(jwtTokenProvider.generateToken(any())).thenReturn("token");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("kristiyan");
        existing.setRole(Role.USER);

        when(userRepository.findByAuthProviderAndProviderSubject("GOOGLE", sub))
                .thenReturn(Optional.of(existing));

        when(jwtTokenProvider.generateToken(any(Authentication.class)))
                .thenReturn("app-jwt");

        // act
        LoginResult result = authService.loginWithGoogle(new GoogleOidcLoginCommand(idToken));

        // assert
        assertEquals(1L, result.id());
        assertEquals("kristiyan", result.username());
        assertEquals(Role.USER, result.role());
        assertEquals("app-jwt", result.token());

        verify(userService, never()).createGoogleUser(anyString(), anyString());
    }

    @Test
    void loginWithGoogle_newUser_createsUser() {
        // arrange
        String idToken = "token";
        String sub = "google-sub";
        String email = "a@b.com";

        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(sub);
        when(jwt.getClaimAsString("email")).thenReturn(email);
        when(googleJwtDecoder.decode(idToken)).thenReturn(jwt);
        when(jwtTokenProvider.generateToken(any())).thenReturn("token");

        when(userRepository.findByAuthProviderAndProviderSubject("GOOGLE", sub))
                .thenReturn(Optional.empty());

        User created = new User();
        created.setId(2L);
        created.setUsername("google_user");
        created.setRole(Role.USER);

        when(userService.createGoogleUser(email, sub)).thenReturn(created);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("app-jwt");

        // act
        LoginResult result = authService.loginWithGoogle(new GoogleOidcLoginCommand(idToken));

        // assert
        assertEquals(2L, result.id());
        assertEquals("google_user", result.username());
        assertEquals(Role.USER, result.role());
        assertEquals("app-jwt", result.token());

        verify(userService).createGoogleUser(email, sub);
    }

    @Test
    void loginWithGoogle_invalidToken_throws() {
        // arrange
        when(googleJwtDecoder.decode(anyString())).thenThrow(new JwtException("bad token"));


        assertThrows(JwtException.class,
                () -> authService.loginWithGoogle(new GoogleOidcLoginCommand("bad")));

        verifyNoInteractions(userRepository, userService, jwtTokenProvider);
    }
}
