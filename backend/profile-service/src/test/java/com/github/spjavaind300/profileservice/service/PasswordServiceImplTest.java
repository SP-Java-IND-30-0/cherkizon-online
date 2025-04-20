package com.github.spjavaind300.profileservice.service;

import com.github.spjavaind300.profileservice.client.AuthClient;
import com.github.spjavaind300.profileservice.dto.UpdatePasswordDTO;
import com.github.spjavaind300.profileservice.service.impl.PasswordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {


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

        when(authClient.changePassword(userId, dto)).thenReturn(ResponseEntity.ok().build());

        passwordService.changePassword(userId, dto);

        verify(authClient).changePassword(userId, dto);
    }
}


