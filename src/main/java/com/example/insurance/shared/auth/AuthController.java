package com.example.insurance.shared.auth;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.domain.user.service.UserService;
import com.example.insurance.global.config.CustomUserDetails;
import com.example.insurance.infrastructure.web.user.UserMapper;
import com.example.insurance.shared.kernel.utils.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class AuthController {

    private final UserService userService;

    private final ApiLogoutHandler apiLogoutHandler;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.getUserByUserId(userDetails.getUserEntity().getUserId());
        return ResponseEntity.ok(
                ResponseBuilder.buildSuccess(request, Map.of("user", UserMapper.toAuthResponseDto(user)),
                        "User is Authenticated",
                        HttpStatus.OK));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {

        apiLogoutHandler.logout(request, response, authentication);

        return ResponseEntity.ok()
                .body(ResponseBuilder.buildSuccess(request, null,
                        "You've logged out successfully",
                        HttpStatus.OK));
    }

}
