package com.torotech.enterpriseops.service;

import com.torotech.enterpriseops.dto.request.UpdateUserRequest;
import com.torotech.enterpriseops.dto.response.UserResponse;
import com.torotech.enterpriseops.entity.User;
import com.torotech.enterpriseops.exception.UserNotFoundException;
import com.torotech.enterpriseops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Obtiene el perfil del usuario autenticado por email.
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
        return toResponse(user);
    }

    /**
     * Lista todos los usuarios de forma paginada.
     * Solo ADMIN puede acceder (controlado en el controller).
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    /**
     * Obtiene un usuario por ID.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    /**
     * Actualiza un usuario.
     * Reglas:
     *  - ADMIN puede cambiar fullName, role y active de cualquier usuario.
     *  - USER solo puede cambiar su propio fullName (no role, no active).
     *
     * Aplicamos patch semantics: solo se actualizan los campos no-null.
     */
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request, String currentUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("email", currentUserEmail));

        boolean isAdmin = currentUser.getRole() == com.torotech.enterpriseops.entity.Role.ADMIN;
        boolean isSelf = currentUser.getId().equals(user.getId());

        // Actualizar fullName (todos pueden si es su propio perfil)
        if (request.fullName() != null && (isAdmin || isSelf)) {
            user.setFullName(request.fullName().trim());
        }

        // Solo ADMIN puede cambiar role
        if (request.role() != null) {
            if (!isAdmin) {
                throw new SecurityException("Solo un ADMIN puede cambiar el rol de un usuario");
            }
            user.setRole(request.role());
        }

        // Solo ADMIN puede cambiar active
        if (request.active() != null) {
            if (!isAdmin) {
                throw new SecurityException("Solo un ADMIN puede cambiar el estado de un usuario");
            }
            user.setActive(request.active());
        }

        User updated = userRepository.save(user);
        log.info("Usuario actualizado: {} por {}", updated.getEmail(), currentUserEmail);

        return toResponse(updated);
    }

    /**
     * Soft delete: marca el usuario como inactivo en vez de borrarlo.
     * Preserva integridad referencial (proyectos, tareas, audit logs).
     */
    @Transactional
    public void deleteUser(UUID id, String currentUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (user.getEmail().equalsIgnoreCase(currentUserEmail)) {
            throw new IllegalStateException("Un usuario no puede eliminarse a sí mismo");
        }

        user.setActive(false);
        userRepository.save(user);

        log.info("Usuario desactivado: {} por {}", user.getEmail(), currentUserEmail);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt()
        );
    }
}