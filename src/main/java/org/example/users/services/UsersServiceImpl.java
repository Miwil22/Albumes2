package org.example.users.services;

import org.example.users.dto.UserRequest;
import org.example.users.dto.UserResponse;
import org.example.users.exceptions.UserNameOrEmailExists;
import org.example.users.exceptions.UserNotFound;
import org.example.users.mappers.UsersMapper;
import org.example.users.models.User;
import org.example.users.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todos los usuarios con username: {}, email: {}, isDeleted: {}", username, email, isDeleted);

        // Criterio de búsqueda por nombre de usuario
        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(u -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + u.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por email
        Specification<User> specEmailUser = (root, query, criteriaBuilder) ->
                email.map(e -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + e.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por borrado
        Specification<User> specIsDeletedUser = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> criterio = Specification.where(specUsernameUser)
                .and(specEmailUser)
                .and(specIsDeletedUser);

        return usersRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
    }

    @Override
    @Cacheable(key = "#id")
    public UserResponse findById(Long id) {
        log.info("Buscando usuario por id: {}", id);
        return usersMapper.toUserResponse(usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id)));
    }

    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario: {}", userRequest);
        usersRepository.findByUsername(userRequest.getUsername())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("El usuario con username " + userRequest.getUsername() + " ya existe");
                });
        usersRepository.findByEmail(userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("El usuario con email " + userRequest.getEmail() + " ya existe");
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest)));
    }

    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Actualizando usuario por id: {}", id);
        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        usersRepository.findByUsername(userRequest.getUsername())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        throw new UserNameOrEmailExists("El usuario con username " + userRequest.getUsername() + " ya existe");
                    }
                });

        usersRepository.findByEmail(userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        throw new UserNameOrEmailExists("El usuario con email " + userRequest.getEmail() + " ya existe");
                    }
                });

        User userUpdated = usersMapper.toUser(userRequest);
        userUpdated.setId(id);
        userUpdated.setCreatedAt(user.getCreatedAt()); // Mantenemos la fecha de creación

        return usersMapper.toUserResponse(usersRepository.save(userUpdated));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando usuario por id: {}", id);
        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        // Borrado lógico o físico, el profesor aquí hace un borrado lógico si se lo indica en el modelo,
        // pero en este método en concreto en su código suele hacer borrado lógico seteando isDeleted a true y salvando

        // Pero viendo el código del profesor, si usa borrado lógico:
        if (usersRepository.findById(id).isEmpty()) {
            throw new UserNotFound(id);
        }
        // usersRepository.deleteById(id); // Si fuera físico

        // El profesor implementa borrado lógico cambiando el flag
        user.setIsDeleted(true);
        usersRepository.save(user);
    }
}