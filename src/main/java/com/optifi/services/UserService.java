package com.optifi.services;

import com.optifi.models.Role;
import com.optifi.models.User;

public interface UserService {

    User createUser(String username, String password, Role role);
}
