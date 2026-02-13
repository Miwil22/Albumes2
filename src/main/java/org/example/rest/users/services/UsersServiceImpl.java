package org.example.rest.users.services;

import org.example.rest.albumes.repositories.AlbumRepository;
import org.example.rest.users.dto.UserInfoResponse;
import org.example.rest.users.dto.UserRequest;
import org.example.rest.users.dto.UserResponse;
import org.example.rest.users.exceptions.UserNameOrEmailExists;
import org.example.rest.users.exceptions.UserNotFound;
import org.example.rest.users.mappers.UsersMapper;
import org.example.rest.users.models.User;
import org.example.rest.users.repositories.UsersRepository;
import jakarta.transaction.Transactional;
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

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final AlbumRepository albumRepository;

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todos los usuarios con username: {} y borrados: {}", username, isDeleted);

        // Criterio de búsqueda por nombre
        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por email
        Specification<User> specEmailUser = (root, query, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por borrado
        Specification<User> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Combinamos las especificaciones
        Specification<User> criterio = Specification.allOf(
                specUsernameUser,
                specEmailUser,
                specIsDeleted
        );

        return usersRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
    }

    @Override
    @Cacheable(key = "#id")
    public UserInfoResponse findById(Long id) {
        log.info("Buscando usuario por id: {}", id);

        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        // Buscamos sus álbumes (si es un manager)
        List<String> albumes = albumRepository.findByUsuarioId(id)
                .stream()
                .map(a -> a.getTitulo())
                .toList();

        return usersMapper.toUserInfoResponse(user, albumes);
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario: {}", userRequest);

        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                });

        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest)));
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Actualizando usuario: {}", userRequest);
        usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                    }
                });

        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest, id)));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        log.info("Borrando usuario por id: {}", id);
        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        // Hacemos el borrado físico si no hay álbumes asociados
        if (albumRepository.existsByUsuarioId(id)) {
            // Si tiene álbumes, lo marcamos como borrado lógico
            log.info("Borrado lógico de usuario por id: {}", id);
            usersRepository.updateIsDeletedToTrueById(id);
        } else {
            // Si no tiene álbumes, lo borramos físicamente
            log.info("Borrado físico de usuario por id: {}", id);
            usersRepository.delete(user);
        }
    }

    @Override
    public List<User> findAllActiveUsers() {
        log.info("Buscando todos los usuarios activos");
        return usersRepository.findAllByIsDeletedFalse();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public void save(User user) {
        usersRepository.save(user);
    }
}
