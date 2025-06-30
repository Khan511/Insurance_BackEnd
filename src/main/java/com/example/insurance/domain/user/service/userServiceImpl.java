package com.example.insurance.domain.user.service;

import static com.example.insurance.global.config.enums.LoginType.LOGIN_FAILURE;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.insurance.common.enummuration.RoleType;
import com.example.insurance.common.enummuration.UserStatus;
import com.example.insurance.domain.role.model.RoleEntity;
import com.example.insurance.domain.role.repository.RoleRepository;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.repository.UserRepository;
import com.example.insurance.global.cache.CacheStore;
import com.example.insurance.global.config.enums.LoginType;
import com.example.insurance.shared.kernel.embeddables.PersonName;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class userServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CacheStore<String, Integer> userCache;

    public User createUserWithRoles(String firstName, String lastName, String email, String password) {

        User user = new User();
        user.setEmail(email);

        PersonName name = new PersonName();
        name.setFirstName(firstName);
        name.setLastName(lastName);
        user.setName(name);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        RoleEntity role = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);

        return userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByUserId(String userId) {
        return userRepository.findUserByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                // If the user is not found in the cache (i.e., it's their first attempt),
                // reset their login attempts to 0 and unlock their account.
                if (userCache.get(user.getEmail()) == null) {
                    user.setLoginAttempts(0);
                    user.setAccountNonLocked(true);
                }
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                userCache.put(user.getEmail(), user.getLoginAttempts());
                // If the login attempts exceed 5, lock the user's account.
                if (userCache.get(user.getEmail()) > 5) {
                    user.setAccountNonLocked(false);
                }
            }
            case LOGIN_FAILURE -> {
                // If the user is not found in the cache (i.e., it's their first attempt),
                // reset their login attempts to 0 and unlock their account.
                if (userCache.get(user.getEmail()) == null) {
                    user.setLoginAttempts(0);
                    user.setAccountNonLocked(true);
                }
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                userCache.put(user.getEmail(), user.getLoginAttempts());
                // If the login attempts exceed 5, lock the user's account.
                if (userCache.get(user.getEmail()) > 5) {
                    user.setAccountNonLocked(false);
                }

            }
            case LOGIN_SUCCESS -> {
                // Handle successful login case
                // If the login is successful, ensure the account is unlocked.
                user.setLoginAttempts(0);
                user.setAccountNonLocked(true);
                user.setLastLogin(LocalDateTime.now());
                // Remove the user from the cache since their login was successful.
                userCache.evict(user.getEmail());
            }
        }
        userRepository.save(user);
    }
}
