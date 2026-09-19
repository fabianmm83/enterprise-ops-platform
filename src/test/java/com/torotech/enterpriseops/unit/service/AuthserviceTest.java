package com.torotech.enterpriseops.unit.service;

import com.torotech.enterpriseops.dto.request.LoginRequest;
import com.torotech.enterpriseops.dto.request.RegisterRequest;
import com.torotech.enterpriseops.dto.response.AuthResponse;
import com.torotech.enterpriseops.entity.Role;
import com.torotech.enterpriseops.entity.User;
import com.torotech.enterpriseops.exception.EmailAlreadyExistsException;
import com.torotech.enterpriseops.repository.UserRepository;
import com.torotech.enterpriseops.security.JwtService;
import com.torotech.enterpriseops.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User savedUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "test@example.com",
                "Password123!",
                "Test User"
        );

        savedUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("$2a$12$hashed")
                .fullName("Test User")
                .role(Role.USER)
                .active(true)
                .build();

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@example.com")
                .password("$2a$12$hashed")
                .authorities("ROLE_USER")
                .build();
    }

    // ============ REGISTER TESTS ============

    @Test
    @DisplayName("Register: crea usuario exitosamente con datos válidos")
    void register_WithValidData_ReturnsAuthResponse() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("$2a$12$hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationMs()).thenReturn(900000L);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900000L);
        assertThat(response.user().email()).isEqualTo("test@example.com");
        assertThat(response.user().role()).isEqualTo(Role.USER);

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("Password123!");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateAccessToken(any(UserDetails.class));
        verify(jwtService).generateRefreshToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Register: lanza EmailAlreadyExistsException si el email ya existe")
    void register_WithExistingEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("test@example.com");

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Register: normaliza el email a lowercase antes de buscar")
    void register_WithUppercaseEmail_NormalizesToLowercase() {
        // Arrange
        RegisterRequest upperEmail = new RegisterRequest(
                "TEST@EXAMPLE.COM",
                "Password123!",
                "Test User"
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$12$hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh");
        when(jwtService.getAccessTokenExpirationMs()).thenReturn(900000L);

        // Act
        authService.register(upperEmail);

        // Assert
        verify(userRepository).existsByEmail("test@example.com");
    }

    // ============ LOGIN TESTS ============

    @Test
    @DisplayName("Login: devuelve tokens con credenciales válidas")
    void login_WithValidCredentials_ReturnsAuthResponse() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationMs()).thenReturn(900000L);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.user().email()).isEqualTo("test@example.com");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Login: propaga BadCredentialsException con credenciales inválidas")
    void login_WithInvalidCredentials_ThrowsBadCredentialsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act + Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
    }
}