package com.github.spjavaind300.controller;


import com.github.spjavaind300.model.dto.AdForNotificationService;
import com.github.spjavaind300.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/ads")
@RequiredArgsConstructor
public class InternalController {

    private final AdService service;


    @GetMapping("/{id}")
    public ResponseEntity<AdForNotificationService> getAdv(@PathVariable int id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getAdForNotifications(id));

    }

    @GetMapping("/check/{id}")
    public ResponseEntity<Void> checkAd(@PathVariable("id") int id) {
        service.getAdForNotifications(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
