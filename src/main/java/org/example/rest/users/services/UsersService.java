package org.example.rest.users.services;

import org.example.rest.users.dto.UserInfoResponse;
import org.example.rest.users.dto.UserRequest;
import org.example.rest.users.dto.UserResponse;
import org.example.rest.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsersService {

    Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable);

    UserInfoResponse findById(Long id);

    UserResponse save(UserRequest userRequest);

    UserResponse update(Long id, UserRequest userRequest);

    void deleteById(Long id);

    List<User> findAllActiveUsers();

    // Servicios usados en la parte webapp
    Optional<User> findByUsername(String username);
    void save(User user);
}