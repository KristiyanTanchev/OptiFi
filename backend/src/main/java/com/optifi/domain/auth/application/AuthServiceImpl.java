package com.optifi.domain.auth.application;

import com.optifi.domain.auth.application.command.GoogleOidcLoginCommand;
import com.optifi.domain.user.model.Role;
import com.optifi.domain.user.model.User;
import com.optifi.domain.user.repository.UserRepository;
import com.optifi.security.CustomUserDetails;
import com.optifi.security.JwtTokenProvider;
import com.optifi.domain.user.application.UserService;
import com.optifi.domain.auth.application.command.LoginCommand;
import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.domain.auth.application.result.LoginResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtDecoder googleJwtDecoder;
    private final UserService userService;
    private final UserRepository userRepository;

    public LoginResult login(LoginCommand cmd) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cmd.username(), cmd.password())
        );

        String jwt = jwtTokenProvider.generateToken(authentication);
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return new LoginResult(principal.getId(), principal.getUsername(), principal.getRole().name(), jwt);
    }

    @Override
    public LoginResult register(RegisterUserCommand cmd) {
        userService.createUser(cmd, Role.USER);

        return login(new LoginCommand(cmd.username(), cmd.password()));
    }

    @Override
    public LoginResult loginWithGoogle(GoogleOidcLoginCommand cmd) {
        Jwt googleJwt = googleJwtDecoder.decode(cmd.idToken());

        String sub = googleJwt.getSubject();
        String email = googleJwt.getClaimAsString("email");

        User user = (User) userRepository
                .findByAuthProviderAndProviderSubject("GOOGLE", sub)
                .orElseGet(() -> userService.createGoogleUser(email, sub));

        CustomUserDetails principal = CustomUserDetails.fromUser(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        String jwt = jwtTokenProvider.generateToken(authentication);

        return new LoginResult(user.getId(), user.getUsername(), user.getRole().name(), jwt);
    }
}
