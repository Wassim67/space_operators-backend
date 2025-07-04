package com.spaceoperators.controller;

import com.spaceoperators.model.entity.Player;
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
            String playerName = payload.get("playerName");
            String password = payload.get("password");

            if (email == null || playerName == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tous les champs sont obligatoires"));
            }

            authService.register(email, playerName, password, "ROLE_USER");
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
            System.out.println("ICI : email: " + email + " password: " + password);
            String token = authService.login(email, password);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            System.out.println("ERREUR LOGIN BACK : " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }



}
