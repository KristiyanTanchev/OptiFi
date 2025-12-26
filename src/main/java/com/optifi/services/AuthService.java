package com.optifi.services;

import com.optifi.services.commands.LoginCommand;
import com.optifi.services.commands.RegisterUserCommand;
import com.optifi.services.results.LoginResult;

public interface AuthService {
    LoginResult login(LoginCommand cmd);

    LoginResult register(RegisterUserCommand cmd);
}
