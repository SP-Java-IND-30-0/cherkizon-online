package com.github.spjavaind300.controller;

import com.github.spjavaind300.service.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//TODO temporary class
@RestController
@RequestMapping("/internal/security")
@RequiredArgsConstructor
public class SecurityController {

    private final JwtUtils jwtUtils;

    @GetMapping
    public String login(@RequestParam long userId, @RequestParam String role) {
        return jwtUtils.generateToken(userId, role);
    }

}
