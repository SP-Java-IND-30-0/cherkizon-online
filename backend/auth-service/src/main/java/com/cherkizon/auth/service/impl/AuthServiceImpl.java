package com.cherkizon.auth.service.impl;

import com.cherkizon.auth.dto.LoginRequest;
import com.cherkizon.auth.dto.RegisterRequest;
import com.cherkizon.auth.dto.response.JwtResponse;
import com.cherkizon.auth.entity.User;
import com.cherkizon.auth.exception.UserAlreadyExistsException;
import com.cherkizon.auth.exception.UserNotFoundException;
import com.cherkizon.auth.exception.InvalidCredentialsException;
import com.cherkizon.auth.repository.UserRepository;
import com.cherkizon.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User with email " + request.getUsername() + " already exists");
        }

        User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setPhone(request.getPhone());
                user.setRole(User.Role.valueOf(request.getRole()));


        return userRepository.save(user).getId();
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getUsername()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Здесь должна быть реальная генерация JWT токена
        String token = "real-jwt-token-" + user.getId(); // Заглушка

        return new JwtResponse(token);
    }
}