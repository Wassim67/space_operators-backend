package com.spaceoperators.service;

import com.spaceoperators.model.entity.GameSession;
import com.spaceoperators.model.entity.Player;
import com.spaceoperators.model.entity.Questions;
import com.spaceoperators.model.response.OperationMessage;
import com.spaceoperators.repository.QuestionsRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {
    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();
    private final QuestionsRepository questionRepository;

    public SessionService(QuestionsRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void createSession(String gameId, List<List<OperationMessage>> turns) {
        gameSessions.put(gameId, new GameSession(turns));
    }

    public GameSession getGameSession(String gameId) {
        return gameSessions.get(gameId);
    }

    public void endSession(String gameId) {
        gameSessions.remove(gameId);
    }

    // Charge N questions aléatoires depuis la BDD
    public List<Questions> loadRandomQuestions(int n) {
        List<Questions> allQuestions = questionRepository.findAll();
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(n, allQuestions.size()));
    }

    public List<List<OperationMessage>> generateTurnsMocks(List<Player> players) {
        List<List<OperationMessage>> allTurns = new ArrayList<>();
        int[] durations = {10, 8, 7, 6, 5};

        for (int t = 0; t < 5; t++) {
            List<OperationMessage> turn = new ArrayList<>();
            Collections.shuffle(players);

            Player operator = players.get(0);
            Player inspector = players.get(1);

            String operatorOpId = "OP-" + (t + 1);

            turn.add(new OperationMessage(
                    t + 1,
                    "inspector",
                    "IN-" + (t + 1),
                    durations[t],
                    "Appuie sur le bouton bleu puis le rouge",
                    List.of(
                            new OperationMessage.Element("button", 6, "color", "#0000FF"),  // Vrai bouton bleu
                            new OperationMessage.Element("button", 7, "color", "#FF0000"),  // Faux bouton rouge
                            new OperationMessage.Element("button", 8, "color", "#00FF00")   // Faux bouton vert
                    ),
                    new OperationMessage.Result(
                            new OperationMessage.ButtonResult("ordered", List.of(6, 7)), // Ordre correct (bleu puis switch 1)
                            new ArrayList<>(),
                            new ArrayList<>()
                    ),
                    inspector.getPlayerId(),
                    operatorOpId
            ));

            turn.add(new OperationMessage(
                    t + 1,
                    "operator",
                    operatorOpId,
                    durations[t],
                    "",
                    List.of(
                            new OperationMessage.Element("button", 6, "color", "#0000FF"), // vrai bouton bleu
                            new OperationMessage.Element("button", 7, "color", "#FF0000"), // faux bouton rouge
                            new OperationMessage.Element("button", 8, "color", "#00FF00")  // faux bouton vert
                    ),
                    new OperationMessage.Result(
                            new OperationMessage.ButtonResult("ordered", List.of(6)), // Seul le bouton 6 est valide
                            new ArrayList<>(),
                            new ArrayList<>()
                    ),
                    operator.getPlayerId()
            ));

            allTurns.add(turn);
        }

        return allTurns;
    }


    private static final Map<String, String> COLOR_MAP;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("Rouge", "#FF0000");
        map.put("Bleu", "#0000FF");
        map.put("Vert", "#00FF00");
        map.put("Jaune", "#FFFF00");
        map.put("Orange", "#FFA500");
        map.put("Violet", "#800080");
        map.put("Cyan", "#00FFFF");
        map.put("Rose", "#FFC0CB");
        map.put("Marron", "#8B4513");
        map.put("Noir", "#000000");
        map.put("Blanc", "#FFFFFF");
        map.put("Gris", "#808080");
        map.put("Magenta", "#FF00FF");
        map.put("Turquoise", "#40E0D0");
        map.put("Indigo", "#4B0082");
        map.put("Or", "#FFD700");
        map.put("Argent", "#C0C0C0");
        map.put("Beige", "#F5F5DC");
        map.put("Corail", "#FF7F50");
        map.put("Lilas", "#C8A2C8");
        map.put("Kaki", "#F0E68C");
        map.put("Sienne", "#A0522D");
        map.put("Bordeaux", "#800000");
        map.put("Crème", "#FFFDD0");
        map.put("Vert clair", "#90EE90");
        map.put("Bleu clair", "#ADD8E6");
        map.put("Rouge foncé", "#8B0000");
        map.put("Vert foncé", "#006400");
        map.put("Bleu marine", "#000080");
        map.put("Saumon", "#FA8072");
        map.put("Lavande", "#E6E6FA");
        map.put("Chartreuse", "#7FFF00");
        map.put("Orangé clair", "#FFDAB9");
        map.put("Turquoise clair", "#AFEEEE");
        map.put("Gris clair", "#D3D3D3");
        map.put("Gris foncé", "#A9A9A9");
        map.put("Mauve", "#E0B0FF");
        map.put("Sable", "#F4A460");
        map.put("Vert olive", "#808000");
        map.put("Bleu ciel", "#87CEEB");
        map.put("Fuchsia", "#FF00FF");
        map.put("Rouge tomate", "#FF6347");
        map.put("Jaune pâle", "#FFFFE0");
        map.put("Brun clair", "#D2B48C");
        map.put("Turquoise foncé", "#00CED1");
        map.put("Bleu pétrole", "#003366");
        map.put("Bleu azur", "#007FFF");
        map.put("Vert printemps", "#00FF7F");
        map.put("Rouge clair", "#FF6961");
        map.put("Pêche", "#FFE5B4");
        map.put("Jaune moutarde", "#FFDB58");
        map.put("Gris bleuté", "#6699CC");
        map.put("Vert sapin", "#013220");
        map.put("Rose vif", "#FF1493");
        map.put("Bleu roi", "#4169E1");
        map.put("Blanc cassé", "#FDF5E6");
        map.put("Noir charbon", "#222222");
        map.put("Rouge brique", "#B22222");
        map.put("Vert menthe", "#98FF98");
        map.put("Bleu turquoise", "#48D1CC");
        map.put("Jaune soleil", "#FFFD37");
        map.put("Gris souris", "#BEBEBE");
        COLOR_MAP = Collections.unmodifiableMap(map);
    }

    // Génère les tours à partir des questions récupérées
    public List<List<OperationMessage>> generateTurnsFromDb(List<Player> players, int nbTurns) {
        List<List<OperationMessage>> allTurns = new ArrayList<>();
        int[] durations = {10, 8, 7, 6, 5};

        List<Questions> questions = loadRandomQuestions(nbTurns);

        for (int t = 0; t < questions.size(); t++) {
            Questions question = questions.get(t);

            List<OperationMessage> turn = new ArrayList<>();
            Collections.shuffle(players);

            Player operator = players.get(0);
            Player inspector = players.get(1);

            String operatorOpId = "OP-" + (t + 1);

            // Parse correct_option_index varchar "1,3" en liste d'entiers [1, 3]
            List<Integer> validButtonIds = new ArrayList<>();
            String[] parts = question.getCorrectOptionIndex().split(",");
            for (String part : parts) {
                try {
                    validButtonIds.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException e) {
                    // Ignore ou log erreur si besoin
                }
            }

            // Création des boutons avec couleur RGB et texte
            List<OperationMessage.Element> elements = new ArrayList<>();
            for (int i = 0; i < question.getOptions().size(); i++) {
                int btnId = i + 1;
                String optionText = question.getOptions().get(i);
                String rgbColor = COLOR_MAP.getOrDefault(optionText, "#FFFFFF"); // blanc par défaut

                // Envoie couleur dans value, texte dans type (à adapter selon front)
                elements.add(new OperationMessage.Element("button", btnId, optionText, rgbColor));
            }

            // Inspector: question + boutons
            turn.add(new OperationMessage(
                    t + 1,
                    "inspector",
                    "IN-" + (t + 1),
                    durations[Math.min(t, durations.length - 1)],
                    question.getQuestion(),
                    elements,
                    new OperationMessage.Result(
                            new OperationMessage.ButtonResult("ordered", validButtonIds),
                            new ArrayList<>(),
                            new ArrayList<>()
                    ),
                    inspector.getPlayerId(),
                    operatorOpId
            ));

            // Operator: boutons même chose
            turn.add(new OperationMessage(
                    t + 1,
                    "operator",
                    operatorOpId,
                    durations[Math.min(t, durations.length - 1)],
                    "",
                    elements,
                    new OperationMessage.Result(
                            new OperationMessage.ButtonResult("ordered", validButtonIds),
                            new ArrayList<>(),
                            new ArrayList<>()
                    ),
                    operator.getPlayerId()
            ));

            allTurns.add(turn);
        }

        return allTurns;
    }




}
