package com.spaceoperators.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Random;

import com.spaceoperators.dao.GameDao;
import com.spaceoperators.dao.SessionDao;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/create-game")
public class CreateGame {

    private final GameDao gameDao;
    private final SessionDao sessionDao;

    @Autowired
    public CreateGame(GameDao gameDao, SessionDao sessionDao) {
        this.gameDao = gameDao;
        this.sessionDao = sessionDao;
    }

    // Endpoint accessible via /create-game qui crée un jeu, puis une session
    @PostMapping
    public int createGame() {
        // Crée un numéro aléatoire pour le jeu
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000); // Génère un nombre entre 1000 et 9999

        // Insérer un nouveau jeu dans la base de données (id_game, theme, etc.)
        String choiceTheme = "Space Adventure";  // Exemple de thème pour le jeu
        int integrity = 100;
        String gameName = "space test";  // Exemple de nom du jeu
        String state = "in_progress";  // Exemple d'état du jeu

        // Insérer le jeu dans la table game
        gameDao.insertGame(randomNumber, choiceTheme, integrity, gameName, state, null);

        // Insère une session liée à ce jeu
        // sessionDao.insertSession(String.valueOf(randomNumber), null);

        return randomNumber; // Retourne le code du jeu créé (id_game)
    }
}
