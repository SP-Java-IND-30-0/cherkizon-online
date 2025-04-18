package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.client.AuthClient;
import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;
import com.github.spjavaind300.profileservice.service.impl.JwtServiceImpl;
import com.github.spjavaind300.profileservice.service.impl.PasswordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {

    @Mock
    private JwtServiceImpl jwtService;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    @Test
    void changePassword_shouldCallAuthClientWithCorrectData() {
        String token = "validToken";
        long userId = 1L;
        UpdatePasswordDTO passwordDTO = new UpdatePasswordDTO();
        passwordDTO.setCurrentPassword("Current01");
        passwordDTO.setNewPassword("NewPassword02");

        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        jwtUserInfo.setUserId(userId);

        when(jwtService.parseToken(token)).thenReturn(jwtUserInfo);

        passwordService.changePassword(token, passwordDTO);

        verify(jwtService).parseToken(token);
        verify(authClient).changePassword(userId, passwordDTO);
    }
}

