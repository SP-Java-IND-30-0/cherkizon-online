package com.github.spjavaind300.notificationservice.client;

import com.github.spjavaind300.notificationservice.model.dto.AdvDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ads-service")
public interface AdvServiceClient {

    @GetMapping("/internal/adv/{id}")
    AdvDto getAdv(@PathVariable("id") long id);
}
