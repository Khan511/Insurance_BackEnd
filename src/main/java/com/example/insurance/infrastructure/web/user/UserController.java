package com.example.insurance.infrastructure.web.user;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.domain.user.service.UserService;
import com.example.insurance.shared.kernel.utils.ResponseBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequestDto userDto, HttpServletRequest request) {

        userService.createUserWithRoles(userDto.firstName(), userDto.lastName(), userDto.email(),
                userDto.password());

        return ResponseEntity.ok().body(ResponseBuilder.buildSuccess(request, Map.of(),
                "User Created, Please check email for account verification.", HttpStatus.CREATED));
    }

}
