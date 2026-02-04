package org.example.users.mappers;

import org.example.users.dto.UserInfoResponse;
import org.example.users.dto.UserRequest;
import org.example.users.dto.UserResponse;
import org.example.users.models.Role;
import org.example.users.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersMapper {

    public User toUser(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole() != null ? Role.valueOf(request.getRole().toUpperCase()) : Role.USER)
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    public UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .roles(List.of(user.getRole().name()))
                .build();
    }
}
