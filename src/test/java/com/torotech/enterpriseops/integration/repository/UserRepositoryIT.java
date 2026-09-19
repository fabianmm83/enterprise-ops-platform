package com.torotech.enterpriseops.integration.repository;

import com.torotech.enterpriseops.config.TestcontainersConfig;
import com.torotech.enterpriseops.entity.Role;
import com.torotech.enterpriseops.entity.User;
import com.torotech.enterpriseops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
@ActiveProfiles("test")
@DisplayName("UserRepository — Integration Tests")
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@example.com")
                .password("$2a$12$hashed")
                .fullName("Test User")
                .role(Role.USER)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("save: persiste un usuario correctamente")
    void save_PersistsUser() {
        User saved = userRepository.save(testUser);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("findByEmail: encuentra usuario existente")
    void findByEmail_WhenUserExists_ReturnsUser() {
        userRepository.save(testUser);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("findByEmail: retorna empty cuando no existe")
    void findByEmail_WhenUserDoesNotExist_ReturnsEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail: retorna true cuando existe")
    void existsByEmail_WhenUserExists_ReturnsTrue() {
        userRepository.save(testUser);

        boolean exists = userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail: retorna false cuando no existe")
    void existsByEmail_WhenUserDoesNotExist_ReturnsFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }
}