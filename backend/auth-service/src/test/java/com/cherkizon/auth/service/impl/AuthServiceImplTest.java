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
import com.cherkizon.auth.service.EventPublisher;
import com.cherkizon.auth.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_successful() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test@mail.com");
        request.setPassword("Password1");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("+79991234567");

        when(userRepository.findByUsername("test@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password1")).thenReturn("hashedPassword");

        authService.register(request);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("test@mail.com") &&
                        user.getPassword().equals("hashedPassword")
        ));
        verify(eventPublisher).publish(any(UserCreatedDto.class));
    }

    @Test
    void register_userAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test@mail.com");

        when(userRepository.findByUsername("test@mail.com")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void login_successful() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user@mail.com");
        request.setPassword("Password1");

        User user = new User();
        user.setUsername("user@mail.com");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);

        JwtResponse jwtResponse = new JwtResponse("access", "refresh", 1L, null);
        when(jwtService.generateTokens(user)).thenReturn(jwtResponse);

        JwtResponse response = authService.login(request);

        assertEquals("access", response.accessToken());
        assertEquals("refresh", response.refreshToken());
    }

    @Test
    void refreshToken_successful() {
        String token = "Bearer abcdef";
        JwtResponse expected = new JwtResponse("access", "refresh", 1L, null);
        when(jwtService.refreshToken("abcdef")).thenReturn(expected);

        JwtResponse response = authService.refreshToken(token);

        assertEquals("access", response.accessToken());
    }

    @Test
    void changePassword_successful() {
        Long userId = 1L;
        RequestChangePasswordDto dto = new RequestChangePasswordDto("oldPass", "newPass");

        User user = new User();
        user.setPassword("encodedOld");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedOld")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");

        authService.changePassword(userId, dto);

        verify(userRepository).save(argThat(savedUser -> savedUser.getPassword().equals("encodedNew")));
    }

    @Test
    void changePassword_invalidCurrentPassword() {
        Long userId = 1L;
        RequestChangePasswordDto dto = new RequestChangePasswordDto("wrongPass", "newPass");

        User user = new User();
        user.setPassword("encodedOld");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedOld")).thenReturn(false);

        assertThrows(InvalidPassword.class, () -> authService.changePassword(userId, dto));
    }

    @Test
    void changePassword_userNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.changePassword(userId, new RequestChangePasswordDto("a", "b")));
    }

    @Test
    void deleteUser_successful() {
        Long userId = 1L;
        authService.deleteUser(userId);
        verify(userRepository).deleteById(userId);
    }
}