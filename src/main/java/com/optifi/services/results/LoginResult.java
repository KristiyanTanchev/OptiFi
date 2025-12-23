package com.optifi.services.results;

import com.optifi.models.Role;

public record LoginResult (Long id, String username, Role role, String token){}
