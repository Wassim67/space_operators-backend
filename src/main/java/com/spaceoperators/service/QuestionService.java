package com.spaceoperators.service;

import com.spaceoperators.model.EQuestion;
import com.spaceoperators.model.entity.Questions;
import com.spaceoperators.payload.responses.GetQuestionResponseDTO;
import com.spaceoperators.repository.EQuestionRepository;
import com.spaceoperators.repository.QuestionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.*;

@Service
public class QuestionService {

    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private EQuestionRepository eQuestionRepository;

    /**
     * Valide la réponse du joueur en fonction de la question attendue.
     * @param questionId  L'id de la question
     * @param playerAnswer La réponse du joueur ("button_vert, bouton_jaune, levier_droit")
     * @return true si la réponse est correcte, false sinon
     */
    public boolean validateAnswer(int questionId, String playerAnswer) {
        Questions questionEntity = questionsRepository.findById(questionId).orElse(null);
        if (questionEntity == null) return false;

        String question = questionEntity.getQuestion();
        String[] parts = question.split(";", 2);
        if (parts.length < 2) return false;
        String actionsRaw = parts[1];
        String[] expected = actionsRaw.split(",");
        Set<String> expectedSet = Arrays.stream(expected)
                .map(String::trim)
                .map(s -> s.toLowerCase().replace(" ", "_"))
                .collect(Collectors.toSet());

        String[] playerItems = playerAnswer.split(",");
        Set<String> playerSet = Arrays.stream(playerItems)
                .map(String::trim)
                .map(s -> s.toLowerCase().replace(" ", "_"))
                .collect(Collectors.toSet());

        return playerSet.containsAll(expectedSet);
    }

    public List<GetQuestionResponseDTO> get() {
        List<EQuestion> allQuestions = eQuestionRepository.findAll();
        return allQuestions.stream().map(q -> {
            GetQuestionResponseDTO dto = new GetQuestionResponseDTO();
            dto.setId(q.getId());
            dto.setQuestion(q.getQuestion());
            return dto;
        }).toList();
    }
}
