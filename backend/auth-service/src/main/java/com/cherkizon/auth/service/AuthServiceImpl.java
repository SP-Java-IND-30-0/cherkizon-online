package com.cherkizon.auth.service;

import com.cherkizon.auth.dto.request.LoginRequest;
import com.cherkizon.auth.dto.request.RegisterRequest;
import com.cherkizon.auth.dto.request.response.JwtResponse;
import com.cherkizon.auth.entity.User;
import com.cherkizon.auth.exception.UserAlreadyExistsException;
import com.cherkizon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cherkizon.auth.service.loggerService.ServiceLogger.AUTH;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация нового пользователя
     */
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            AUTH.warn("Registration failed - user exists: {}", request.getUsername());
            throw new UserAlreadyExistsException("User with email " + request.getUsername() + " already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        AUTH.info("User registered: {}", user.getUsername());
    }

    /**
     * Аутентификация пользователя
     */
    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        if (!user.isActive()) {
            AUTH.warn("Login attempt for inactive account: {}", user.getUsername());
            throw new DisabledException("Account is not activated");
        }

        AUTH.info("User logged in: {}", user.getUsername());
        return jwtService.generateTokens(user);
    }
}