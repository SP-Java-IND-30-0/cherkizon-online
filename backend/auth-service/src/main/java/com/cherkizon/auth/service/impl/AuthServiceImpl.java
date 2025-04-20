package com.cherkizon.auth.service.impl;

import com.cherkizon.auth.dto.event.UserCreatedDto;
import com.cherkizon.auth.dto.request.LoginRequest;
import com.cherkizon.auth.dto.request.RegisterRequest;
import com.cherkizon.auth.dto.request.RequestChangePasswordDto;
import com.cherkizon.auth.dto.response.JwtResponse;
import com.cherkizon.auth.entity.User;
import com.cherkizon.auth.exception.InvalidPassword;
import com.cherkizon.auth.exception.NotFoundException;
import com.cherkizon.auth.exception.UserAlreadyExistsException;
import com.cherkizon.auth.repository.UserRepository;
import com.cherkizon.auth.service.AuthService;
import com.cherkizon.auth.service.EventPublisher;
import com.cherkizon.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Registration failed - user exists: {}", request.getUsername());
            throw new UserAlreadyExistsException("User with email " + request.getUsername() + " already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);

        userRepository.save(user);
        eventPublisher.publish(new UserCreatedDto(
                user.getId(),
                user.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone()
        ));
        log.info("User registered: {}", user.getUsername());
    }

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

        log.info("User logged in: {}", user.getUsername());
        return jwtService.generateTokens(user);
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        return jwtService.refreshToken(refreshToken.substring(7));
    }

    @Override
    public void changePassword(Long userId, RequestChangePasswordDto passwords) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        if (!passwordEncoder.matches(passwords.currentPassword(), user.getPassword())) {
            throw new InvalidPassword("Invalid current password");
        }
        user.setPassword(passwordEncoder.encode(passwords.newPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}