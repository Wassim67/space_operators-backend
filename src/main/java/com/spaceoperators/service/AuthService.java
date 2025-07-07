package com.spaceoperators.service;

import com.spaceoperators.repository.PlayerDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.spaceoperators.security.JwtUtil;

@Service
public class AuthService {

    private final PlayerDao playerDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(PlayerDao playerDao, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.playerDao = playerDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(String email, String playerName, String rawPassword, String role) {
        String id = UUID.randomUUID().toString();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        playerDao.registerPlayer(id, email, playerName, hashedPassword, role);
    }

    public boolean authenticate(String email, String rawPassword) {
        Optional<Map<String, Object>> playerOpt = playerDao.getPlayerByEmail(email);
        if (playerOpt.isEmpty()) {
            return false;
        }
        String storedHash = (String) playerOpt.get().get("password");
        return passwordEncoder.matches(rawPassword, storedHash);
    }

    public String login(String email, String rawPassword) {
        Optional<Map<String, Object>> playerOpt = playerDao.getPlayerByEmail(email);
        if (playerOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        String storedHash = (String) playerOpt.get().get("password");
        if (!passwordEncoder.matches(rawPassword, storedHash)) {
            throw new RuntimeException("Invalid credentials");
        }

        String playerId = (String) playerOpt.get().get("playerId");
        String role = (String) playerOpt.get().get("role");

        return jwtUtil.generateToken(playerId, role);
    }

}
