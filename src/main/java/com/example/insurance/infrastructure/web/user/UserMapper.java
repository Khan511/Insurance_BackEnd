package com.example.insurance.infrastructure.web.user;

import java.util.Set;
import java.util.stream.Collectors;
import com.example.insurance.domain.user.model.User;
import com.example.insurance.infrastructure.web.dtos.AuthResponseDto;

public class UserMapper {

    public static AuthResponseDto toAuthResponseDto(User user) {

        Set<String> roleNames = user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream().map(Enum::name)).collect(Collectors.toSet());

        return AuthResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(user.getCreatedAt().toString())
                .roles(roleNames)
                .permissions(permissions)
                .build();
    }
}
