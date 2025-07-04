package com.spaceoperators.controller;

import com.spaceoperators.model.entity.Player;
import com.spaceoperators.payload.responses.GetMeResponseDTO;
import com.spaceoperators.payload.responses.GetQuestionResponseDTO;
import com.spaceoperators.payload.responses.GetUserResponseDTO;
import com.spaceoperators.service.JwtService;
import com.spaceoperators.service.PlayerService;
import com.spaceoperators.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    private UserService userService;
    private PlayerService playerService;
    private JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService, PlayerService playerService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.playerService = playerService;
    }



    @GetMapping("/me2")
    public GetMeResponseDTO me(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwtService.extractUserInformation(jwt);
    }

    @RequestMapping("/user/all")
    @GetMapping
    public List<GetUserResponseDTO> get() {


        return userService.get();
    }

    @GetMapping("/me")
    public Map<String, Object> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName(); // subject du token
        String email = null;
        if (auth.getDetails() instanceof Map<?, ?> detailsMap) {
            email = (String) detailsMap.get("email");
        }

//        List<String> roles = auth.getAuthorities().stream()
//                .map(a -> a.getAuthority().replace("ROLE_", ""))
//                .toList();

       // int gamesPlayed = playerDao.countGamesByPlayerId(username);
        System.out.println("USERNAME email " + username + " " + email);
        return Map.of(
                "username", username
               // "email", email//,
               // "roles", roles,
               // "gamesPlayed", gamesPlayed
        );
    }

    // ✅ GET un joueur par son ID (String)
    @GetMapping("/player/{id}")
    public ResponseEntity<Player> getById(@PathVariable String id) {
        Player player = playerService.findById(id);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ PUT : mise à jour d’un joueur
    @PutMapping("/player/{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody Player player) {
        playerService.update(id, player);
        return ResponseEntity.ok().build();
    }

    // ✅ DELETE : suppression d’un joueur
    @DeleteMapping("/player/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
