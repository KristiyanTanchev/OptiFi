package com.optifi.domain.auth.application;

import com.optifi.domain.auth.application.command.GoogleOidcLoginCommand;
import com.optifi.domain.auth.application.command.LoginCommand;
import com.optifi.domain.auth.application.command.RegisterUserCommand;
import com.optifi.domain.auth.application.result.LoginResult;

public interface AuthService {
    LoginResult login(LoginCommand cmd);

    LoginResult register(RegisterUserCommand cmd);

    LoginResult loginWithGoogle(GoogleOidcLoginCommand cmd);
}
