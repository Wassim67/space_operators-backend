package com.spaceoperators.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Random;

import com.spaceoperators.repository.GameDao;
import com.spaceoperators.repository.SessionDao;

@RestController
@RequestMapping("/api/create-game")
public class GameCreationController {

    private final GameDao gameDao;
    private final SessionDao sessionDao;

    public GameCreationController(GameDao gameDao, SessionDao sessionDao) {
        this.gameDao = gameDao;
        this.sessionDao = sessionDao;
    }

    // Endpoint accessible via /create-game qui crée une partie
    @PostMapping
    public int createGame() {
        // TODO: améliorer la gestion de génération de numéro de partie
        // Crée un numéro aléatoire pour le jeu
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000); // Génère un nombre entre 1000 et 9999

        // TODO : Intégrer la génération de thème avec l'IA
        String choiceTheme = "Space Adventure";  // Temporaire
        int integrity = 100;
        String gameName = "space test";  // Temporaire
        String state = "in_progress";  // Temporaire pour V2

        // Insérer le jeu dans la table game
        gameDao.insertGame(randomNumber, choiceTheme, integrity, gameName, state, null);

        return randomNumber; // Retourne le code du jeu créé (id_game)
    }
}
