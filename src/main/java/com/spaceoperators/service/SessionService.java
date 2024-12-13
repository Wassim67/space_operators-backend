package com.spaceoperators.service;

import com.spaceoperators.model.GameSession;
import com.spaceoperators.model.response.OperationMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private final Map<String, GameSession> gameSessions;

    public SessionService() {
        this.gameSessions = new ConcurrentHashMap<>();
    }

    // Démarrer une nouvelle partie avec un ID unique pour chaque session
    public void startNewGame(String gameId) {
        GameSession session = new GameSession(generateMockTurns());
        gameSessions.put(gameId, session);
    }

    public GameSession getGameSession(String gameId) {
        return gameSessions.get(gameId);
    }


    // Récupérer le prochain tour pour une session spécifique
    public OperationMessage getNextTurn(String gameId) {
        GameSession session = gameSessions.get(gameId);
        if (session == null) {
            return null; // Retourne null si la session n'existe pas
        }
        return session.getNextTurn();
    }

    // Obtenir la durée du tour actuel pour une session spécifique
    public int getDuration(String gameId) {
        GameSession session = gameSessions.get(gameId);
        if (session == null) {
            return -1; // Retourne -1 si la session n'existe pas
        }
        return session.getDuration();
    }

    // Terminer une session de jeu et la supprimer
    public void endSession(String gameId) {
        GameSession session = gameSessions.get(gameId);
        if (session != null) {
            session.endSession();
            gameSessions.remove(gameId);
        }
    }

    // Génération de tours fictifs (mock)
    private List<OperationMessage> generateMockTurns() {
        List<OperationMessage> turns = new ArrayList<>();

        // Tour 1
        OperationMessage turn1 = new OperationMessage(
                1,  // Tour 1
                "operator",  // Rôle "operator"
                "CA-01",  // ID "CA-01"
                10,  // Durée de 10 secondes
                "Appuyer sur les boutons rouges et verts",  // Description
                List.of(  // Elements : uniquement des boutons
                        new OperationMessage.Element("button", 4, "color", "#FF0000"),
                        new OperationMessage.Element("button", 5, "color", "#00FF00")
                ),
                new OperationMessage.Result(  // Résultat avec boutons uniquement
                        new OperationMessage.ButtonResult("random", List.of(5, 4)),
                        new ArrayList<>(),  // Pas de switches
                        new ArrayList<>()
                )
        );
        turns.add(turn1);

        // Tour 2
        OperationMessage turn2 = new OperationMessage(
                2,  // Tour 2
                "operator",  // Rôle "operator"
                "CA-02",  // ID "CA-02"
                10,  // Durée de 10 secondes
                "Appuyer sur le bouton bleu puis le bouton rouge",  // Description
                List.of(  // Elements : uniquement des boutons
                        new OperationMessage.Element("button", 6, "color", "#0000FF"),
                        new OperationMessage.Element("button", 7, "color", "#FF0000")
                ),
                new OperationMessage.Result(  // Résultat avec boutons uniquement
                        new OperationMessage.ButtonResult("ordered", List.of(6, 7)),
                        new ArrayList<>(),  // Pas de switches
                        new ArrayList<>()
                )
        );
        turns.add(turn2);

        // Tour 3
        OperationMessage turn3 = new OperationMessage(
                3,  // Tour 3
                "operator",  // Rôle "operator"
                "CA-03",  // ID "CA-03"
                10,  // Durée de 10 secondes
                "Appuyer deux fois sur le bouton vert",  // Description
                List.of(  // Elements : uniquement des boutons
                        new OperationMessage.Element("button", 8, "color", "#00FF00"),
                        new OperationMessage.Element("button", 9, "color", "#FF0000")
                ),
                new OperationMessage.Result(  // Résultat avec boutons uniquement
                        new OperationMessage.ButtonResult("random", List.of(8, 8, 9)),
                        new ArrayList<>(),  // Pas de switches
                        new ArrayList<>()
                )
        );
        turns.add(turn3);

        // Tour 1
        OperationMessage turn4 = new OperationMessage(
                4,  // Tour 1
                "operator",  // Rôle "operator"
                "CA-01",  // ID "CA-01"
                10,  // Durée de 10 secondes
                "Appuyer sur les boutons rouges et verts",  // Description
                List.of(  // Elements : uniquement des boutons
                        new OperationMessage.Element("button", 4, "color", "#FF0000"),
                        new OperationMessage.Element("button", 5, "color", "#00FF00")
                ),
                new OperationMessage.Result(  // Résultat avec boutons uniquement
                        new OperationMessage.ButtonResult("random", List.of(5, 4)),
                        new ArrayList<>(),  // Pas de switches
                        new ArrayList<>()
                )
        );
        turns.add(turn4);

        return turns;
    }
}
