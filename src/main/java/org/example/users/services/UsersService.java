package org.example.users.services;

import org.example.users.dto.UserRequest;
import org.example.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsersService {
    Page<User> findAll(Optional<String> username, Optional<String> role, Pageable pageable);
    User findById(String id);
    User save(UserRequest userRequest);
    User update(String id, UserRequest userRequest);
    void deleteById(String id);
}
