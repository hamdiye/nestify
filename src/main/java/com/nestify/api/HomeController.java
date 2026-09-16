package com.nestify.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public Map<String, String> home() {
        return Map.of(
                "status", "UP",
                "service", "Nestify API",
                "message", "Backend is running smoothly!");
    }
}