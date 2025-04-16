package com.github.spjavaind300.notificationservice.service.imp;

import com.github.spjavaind300.notificationservice.client.AdvServiceClient;
import com.github.spjavaind300.notificationservice.model.dto.AdvDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdvServiceImpTest {

    @Mock
    private AdvServiceClient client;

    private AdvServiceImp advServiceImp;

    @BeforeEach
    void setUp() {
        advServiceImp = new AdvServiceImp(client);
    }

    @Test
    void getAdv() {

        AdvDto advDto = new AdvDto(1, 1L,"adv_title");
        when(client.getAdv(1)).thenReturn(advDto);

        AdvDto result = advServiceImp.getAdv(1);

        assertNotNull(result);
        assertEquals(advDto, result);

    }
}