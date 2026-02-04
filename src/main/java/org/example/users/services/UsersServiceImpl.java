package org.example.users.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.users.dto.UserRequest;
import org.example.users.exceptions.UserNotFound;
import org.example.users.mappers.UsersMapper;
import org.example.users.models.Role;
import org.example.users.models.User;
import org.example.users.repositories.UserRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> findAll(Optional<String> username, Optional<String> role, Pageable pageable) {
        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(u -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + u.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specRoleUser = (root, query, criteriaBuilder) ->
                role.map(r -> criteriaBuilder.equal(root.get("role"), Role.valueOf(r.toUpperCase())))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> criterio = Specification.where(specUsernameUser).and(specRoleUser);

        return userRepository.findAll(criterio, pageable);
    }

    @Override
    @Cacheable(key = "#id")
    public User findById(String id) {
        log.info("Buscando usuario con id: {}", id);
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFound(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public User save(UserRequest userRequest) {
        log.info("Guardando usuario: {}", userRequest);
        User user = usersMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @CachePut(key = "#id")
    public User update(String id, UserRequest userRequest) {
        log.info("Actualizando usuario con id: {}", id);
        User userToUpdate = findById(id);
        userToUpdate.setUsername(userRequest.getUsername());
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        if (userRequest.getRole() != null) {
            userToUpdate.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        }
        return userRepository.save(userToUpdate);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(String id) {
        log.info("Eliminando usuario con id: {}", id);
        User user = findById(id);
        userRepository.delete(user);
    }
}
