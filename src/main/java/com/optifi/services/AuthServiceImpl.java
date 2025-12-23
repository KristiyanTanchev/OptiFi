package com.optifi.services;

import com.optifi.models.Role;
import com.optifi.models.User;
import com.optifi.security.CustomUserDetails;
import com.optifi.security.JwtTokenProvider;
import com.optifi.services.commands.LoginCommand;
import com.optifi.services.commands.RegisterUserCommand;
import com.optifi.services.results.LoginResult;
import com.optifi.services.results.RegisterUserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public LoginResult login(LoginCommand cmd) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cmd.username(), cmd.password())
        );

        String jwt = jwtTokenProvider.generateToken(authentication);
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return new LoginResult(principal.getId(), principal.getUsername(), principal.getRole(), jwt);
    }

    @Override
    public RegisterUserResult register(RegisterUserCommand cmd) {
        User created = userService.createUser(
                cmd.username(),
                cmd.password(),
                Role.USER
        );

        return RegisterUserResult.fromEntity(created);
    }
}
