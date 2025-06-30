package com.example.insurance.domain.user.service;

import com.example.insurance.domain.user.model.User;
import com.example.insurance.global.config.enums.LoginType;

public interface UserService {

    public User createUserWithRoles(String firstName, String lastName, String email, String password);

    public User getUserByEmail(String email);

    public User getUserByUserId(String userId);

    public void updateLoginAttempt(String email, LoginType loginType);

}
