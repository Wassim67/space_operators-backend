package com.spaceoperators.service;

import com.spaceoperators.model.response.OperationMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {
    private List<OperationMessage> turns;
    private int currentTurn;

    public SessionService() {
        this.turns = generateMockTurns();  // Utilisation des mocks en dur
        this.currentTurn = 0;
    }

    // Générer cinq tours de jeu avec des boutons uniquement
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

        // Tour 4
        OperationMessage turn4 = new OperationMessage(
                4,  // Tour 4
                "instructor",  // Rôle "instructor"
                "CA-04",  // ID "CA-04"
                10,  // Durée de 10 secondes
                "Appuyer deux fois sur le bouton jaune",  // Description
                List.of(  // Elements : uniquement des boutons
                        new OperationMessage.Element("button", 10, "color", "#FFFF00"),
                        new OperationMessage.Element("button", 11, "color", "#FF0000")
                ),
                new OperationMessage.Result(  // Résultat avec boutons uniquement
                        new OperationMessage.ButtonResult("random", List.of(10, 10)),
                        new ArrayList<>(),  // Pas de switches
                        new ArrayList<>()
                )
        );
        turns.add(turn4);

        // Tour 5
        OperationMessage turn5 = new OperationMessage(
                5,  // Tour 5
                "operator",  // Rôle "operator"
                "CA-05",  // ID "CA-05"
                10,  // Durée de 10 secondes
                "Appuyer sur le bouton rouge puis sur le bouton bleu",  // Description
                List.of(  // Elements : uniquement des boutons
                        new OperationMessage.Element("button", 12, "color", "#FF0000"),
                        new OperationMessage.Element("button", 13, "color", "#0000FF")
                ),
                new OperationMessage.Result(  // Résultat avec boutons uniquement
                        new OperationMessage.ButtonResult("ordered", List.of(12, 13)),
                        new ArrayList<>(),  // Pas de switches
                        new ArrayList<>()
                )
        );
        turns.add(turn5);

        return turns;
    }

    public void startNewGame() {
        currentTurn = 0;  // Réinitialise le tour à 0
    }


    public OperationMessage getNextTurn() {
        if (currentTurn < turns.size()) {
            return turns.get(currentTurn++);
        }
        return null;  // Plus de tours disponibles
    }

    public int getDuration() {
        if (currentTurn > 0 && currentTurn <= turns.size()) {
            return turns.get(currentTurn - 1).getData().getDuration();
        }
        return -1;  // Retourne -1 si aucun tour n'est disponible
    }

}
