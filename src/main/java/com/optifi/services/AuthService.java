package com.optifi.services;

import com.optifi.services.commands.LoginCommand;
import com.optifi.services.commands.RegisterUserCommand;
import com.optifi.services.results.LoginResult;
import com.optifi.services.results.RegisterUserResult;

public interface AuthService {
    LoginResult login(LoginCommand cmd);

    RegisterUserResult register(RegisterUserCommand cmd);
}
