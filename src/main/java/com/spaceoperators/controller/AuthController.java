package com.spaceoperators.controller;

import com.spaceoperators.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String name = payload.get("name");
            String password = payload.get("password");

            if (email == null || name == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tous les champs sont obligatoires"));
            }

            authService.register(email, name, password, "ROLE_USER");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String password = payload.get("password");
            String token = authService.login(email, password);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

}
