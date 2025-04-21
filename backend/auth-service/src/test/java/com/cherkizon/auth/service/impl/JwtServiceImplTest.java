package com.cherkizon.auth.service.impl;

import com.cherkizon.auth.dto.response.JwtResponse;
import com.cherkizon.auth.entity.Token;
import com.cherkizon.auth.entity.User;
import com.cherkizon.auth.entity.User.Role;
import com.cherkizon.auth.exception.InvalidTokenException;
import com.cherkizon.auth.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final String secret = Base64.getEncoder()
            .encodeToString("superSecretKeyForJwtTesting1234567890!@".getBytes(StandardCharsets.UTF_8));
    private final long accessExpiration = 1000 * 60 * 15;
    private final long refreshExpiration = 1000 * 60 * 60 * 24;

    private User user;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "accessExpiration", accessExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshExpiration);

        user = new User(1L, "john_doe", "hashed_password", Role.USER, new ArrayList<>());
    }

    @Test
    void generateTokens_shouldReturnValidJwtResponse() {
        JwtResponse response = jwtService.generateTokens(user);

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals(user.getId(), response.userId());
        assertTrue(response.expiresAt().isAfter(Instant.now()));
    }

    @Test
    void refreshToken_shouldReturnNewTokens_whenValid() throws InterruptedException {
        JwtResponse oldTokens = jwtService.generateTokens(user);
        String oldAccessToken = oldTokens.accessToken();
        String oldRefreshToken = oldTokens.refreshToken();
        String hashedToken = "encoded_token";
        Instant validUntil = Instant.now().plusSeconds(3600);
        Token dbToken = Token.builder()
                .id(10L)
                .user(user)
                .refreshToken(hashedToken)
                .expiresAt(validUntil)
                .build();
        when(tokenRepository.findAllByUserId(user.getId())).thenReturn(List.of(dbToken));
        when(passwordEncoder.matches(oldRefreshToken, hashedToken)).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("new_encoded_token");
        Thread.sleep(50);
        JwtResponse newTokens = jwtService.refreshToken(oldRefreshToken);
        assertNotNull(newTokens);
        assertNotEquals(oldAccessToken, newTokens.accessToken(), "Access tokens should be different after refresh");
        assertNotEquals(oldRefreshToken, newTokens.refreshToken(), "Refresh tokens should be different after refresh");
        verify(tokenRepository).delete(dbToken);
        verify(tokenRepository).save(any(Token.class));
    }


    @Test
    void refreshToken_shouldThrow_whenTokenNotInDb() {
        JwtResponse oldTokens = jwtService.generateTokens(user);
        String rawRefreshToken = oldTokens.refreshToken();

        when(tokenRepository.findAllByUserId(user.getId())).thenReturn(List.of());

        InvalidTokenException ex = assertThrows(InvalidTokenException.class,
                () -> jwtService.refreshToken(rawRefreshToken));
        assertEquals("Refresh token not found or invalid", ex.getMessage());
    }

    @Test
    void refreshToken_shouldThrow_whenTokenExpiredInDb() {
        JwtResponse oldTokens = jwtService.generateTokens(user);
        String rawRefreshToken = oldTokens.refreshToken();

        String hashedToken = "encoded_token";
        Instant expiredAt = Instant.now().minusSeconds(10);

        Token dbToken = Token.builder()
                .user(user)
                .refreshToken(hashedToken)
                .expiresAt(expiredAt)
                .build();

        when(tokenRepository.findAllByUserId(user.getId())).thenReturn(List.of(dbToken));
        when(passwordEncoder.matches(rawRefreshToken, hashedToken)).thenReturn(true);

        assertThrows(InvalidTokenException.class, () -> jwtService.refreshToken(rawRefreshToken));
        verify(tokenRepository).delete(dbToken);
    }

    @Test
    void extractUserId_shouldReturnCorrectUserId() {
        JwtResponse response = jwtService.generateTokens(user);
        Long extracted = jwtService.extractUserId(response.accessToken());

        assertEquals(user.getId(), extracted);
    }
}
