package com.github.spjavaind300.notificationservice.service.imp;

import com.github.spjavaind300.notificationservice.client.AdvServiceClient;
import com.github.spjavaind300.notificationservice.model.dto.AdvDto;
import com.github.spjavaind300.notificationservice.service.AdvService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdvServiceImp implements AdvService {

    private final AdvServiceClient client;

    @Override
    public AdvDto getAdv(long advId) {
        return client.getAdv(advId);
    }
}
