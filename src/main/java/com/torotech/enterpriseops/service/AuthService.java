package com.torotech.enterpriseops.service;

import com.torotech.enterpriseops.dto.request.LoginRequest;
import com.torotech.enterpriseops.dto.request.RegisterRequest;
import com.torotech.enterpriseops.dto.response.AuthResponse;
import com.torotech.enterpriseops.dto.response.UserResponse;
import com.torotech.enterpriseops.entity.Role;
import com.torotech.enterpriseops.entity.User;
import com.torotech.enterpriseops.exception.EmailAlreadyExistsException;
import com.torotech.enterpriseops.repository.UserRepository;
import com.torotech.enterpriseops.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName().trim())
                .role(Role.USER)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Nuevo usuario registrado: {}", savedUser.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationMs(),
                toUserResponse(savedUser)
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase().trim();

        // Autenticamos con el AuthenticationManager (lanza BadCredentialsException si falla)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado tras autenticar"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        log.info("Login exitoso: {}", email);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationMs(),
                toUserResponse(user)
        );
    }

    private UserResponse toUserResponse(User user) {
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