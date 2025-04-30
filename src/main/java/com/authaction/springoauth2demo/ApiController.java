package com.authaction.springoauth2demo;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiController {

    // ✅ GET /public → No auth required
    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "This is a public message!");
    }

    // ✅ GET /protected → Requires valid JWT from AuthAction
    @GetMapping("/protected")
    public Map<String, String> protectedEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("message", "This is a protected message!");
    }
}
