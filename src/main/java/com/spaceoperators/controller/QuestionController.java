package com.spaceoperators.controller;

import com.spaceoperators.model.EQuestion;
import com.spaceoperators.payload.responses.GetQuestionResponseDTO;
import com.spaceoperators.repository.EQuestionRepository;
import com.spaceoperators.service.AIFormatterService;
import com.spaceoperators.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private QuestionService questionService;
    private EQuestionRepository eQuestionRepository;
    private AIFormatterService aiFormatterService;

    public QuestionController(QuestionService questionService, EQuestionRepository eQuestionRepository, AIFormatterService aiFormatterService) {
        this.questionService = questionService;
        this.eQuestionRepository = eQuestionRepository;
        this.aiFormatterService = aiFormatterService;
    }

    @PostMapping
    public EQuestion save(@RequestBody EQuestion eQuestion) {
        System.out.println("QUESTION ORIGINALE : " + eQuestion.getQuestion());

        // Appelle IA uniquement pour générer options + indexes corrects (sans reformater la question)
        String prompt = "Génère EXACTEMENT dans ce format : " +
                "\"option1, option2, option3, option4 ; indexes_corrects\" " +
                "où indexes_corrects sont les positions (1-based) des bonnes réponses séparées par des virgules. " +
                "Tu dois toujours fournir 4 options, même si certaines sont fausses. " +
                "Exemple : \"Rouge, Bleu, Vert, Jaune ; 1,2\". " +
                "Ne dépasse pas 255 caractères.";

        String formattedResponse = aiFormatterService.formatQuestionWithGroqWithCustomPrompt(eQuestion.getQuestion(), prompt);

        if (formattedResponse == null || formattedResponse.isBlank()) {
            throw new RuntimeException("Erreur lors du formatage IA, réponse vide");
        }

        // Parsing simple : "option1, option2, option3, option4 ; 1,3"
        String[] parts = formattedResponse.split(";");
        if (parts.length < 2) {
            throw new RuntimeException("Réponse IA mal formatée : " + formattedResponse);
        }

        String optionsPart = parts[0].trim();
        String correctIndexes = parts[1].trim();

        List<String> optionsList = Arrays.stream(optionsPart.split(","))
                .map(String::trim)
                .toList();


        eQuestion.setOptions(optionsList);
        eQuestion.setCorrectOptionIndex(correctIndexes);


        EQuestion saved = eQuestionRepository.save(eQuestion);
        System.out.println("Question sauvegardée : " + saved);
        return saved;
    }




    @RequestMapping("/all")
    @GetMapping
    public List<GetQuestionResponseDTO> get() {

        System.out.println("AVAVAINOVA");

        return questionService.get();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        eQuestionRepository.deleteById(id);
    }


    @GetMapping("/{id}")
    public GetQuestionResponseDTO getById(@PathVariable Long id) {
        EQuestion entity = eQuestionRepository.findById(id).orElseThrow();
        GetQuestionResponseDTO dto = new GetQuestionResponseDTO();
        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());

        return dto;
    }

    @PutMapping("/{id}")
    public EQuestion update(@PathVariable Long id, @RequestBody EQuestion eQuestion) {
        EQuestion existing = eQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        existing.setQuestion(eQuestion.getQuestion());
        existing.setOptions(eQuestion.getOptions());
        existing.setCorrectOptionIndex(eQuestion.getCorrectOptionIndex());

        EQuestion saved = eQuestionRepository.save(existing);
        System.out.println("Question mise à jour : " + saved);
        return saved;
    }


}
