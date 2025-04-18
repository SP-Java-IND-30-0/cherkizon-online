package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.client.AuthClient;
import com.github.spjavaind300.profileservice.dto.JwtUserInfo;
import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;
import com.github.spjavaind300.profileservice.service.impl.JwtUtilsImpl;
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
    private JwtUtilsImpl jwtService;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    @Test
    void changePassword_delegatesToAuthClient() {
        long userId = 123L;
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setCurrentPassword("oldPass");
        dto.setNewPassword("newPass");

        passwordService.changePassword(userId, dto);

        verify(authClient).changePassword(userId, dto);
    }
}

