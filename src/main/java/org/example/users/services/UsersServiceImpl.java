package org.example.users.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.albumes.repositories.AlbumRepository;
import org.example.users.dto.UserInfoResponse;
import org.example.users.dto.UserRequest;
import org.example.users.dto.UserResponse;
import org.example.users.exceptions.UserNotFound;
import org.example.users.exceptions.UsersException;
import org.example.users.mappers.UsersMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final AlbumRepository albumRepository; // Adaptado: Usamos álbumes en vez de tarjetas
    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todos los usuarios con username: {}, email: {}, borrados: {}", username, email, isDeleted);

        Specification<User> specUsername = (root, query, cb) ->
                username.map(m -> cb.like(cb.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> cb.isTrue(cb.literal(true)));

        Specification<User> specEmail = (root, query, cb) ->
                email.map(m -> cb.like(cb.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> cb.isTrue(cb.literal(true)));

        Specification<User> specIsDeleted = (root, query, cb) ->
                isDeleted.map(m -> cb.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> cb.isTrue(cb.literal(true)));

        Specification<User> criterio = Specification.allOf(specUsername, specEmail, specIsDeleted);

        return userRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
    }

    @Override
    @Cacheable(key = "#id")
    public UserInfoResponse findById(String id) {
        log.info("Buscando usuario por id: {}", id);
        UUID uuid = UUID.fromString(id);
        User user = userRepository.findById(uuid).orElseThrow(() -> new UserNotFound(id));

        // Adaptado: Buscamos sus álbumes en lugar de tarjetas
        // Asumiendo que Album tiene un método getTitulo()
        var albumes = albumRepository.findAllByUsuarioId(uuid).stream()
                .map(a -> a.getNombre()) // O getTitulo(), comprueba tu modelo Album
                .toList();

        return usersMapper.toUserInfoResponse(user, albumes);
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario: {}", userRequest);
        userRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UsersException("Ya existe un usuario con ese username o email");
                });

        User user = usersMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return usersMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @CachePut(key = "#id")
    public UserResponse update(String id, UserRequest userRequest) {
        log.info("Actualizando usuario: {}", userRequest);
        UUID uuid = UUID.fromString(id);
        User userActual = userRepository.findById(uuid).orElseThrow(() -> new UserNotFound(id));

        userRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(uuid)) {
                        throw new UsersException("Ya existe un usuario con ese username o email");
                    }
                });

        // Actualizamos los campos
        User userUpdated = usersMapper.toUser(userRequest, userActual);
        // Si la password ha cambiado en el request, hay que codificarla, si no, mantener la antigua
        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            userUpdated.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        } else {
            userUpdated.setPassword(userActual.getPassword());
        }

        return usersMapper.toUserResponse(userRepository.save(userUpdated));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteById(String id) {
        log.info("Borrando usuario por id: {}", id);
        UUID uuid = UUID.fromString(id);
        User user = userRepository.findById(uuid).orElseThrow(() -> new UserNotFound(id));

        // Adaptado: Si tiene álbumes, borrado lógico
        if (albumRepository.existsByUsuarioId(uuid)) {
            log.info("Borrado lógico de usuario por id: {}", id);
            userRepository.updateIsDeletedToTrueById(uuid);
        } else {
            log.info("Borrado físico de usuario por id: {}", id);
            userRepository.delete(user);
        }
    }
}