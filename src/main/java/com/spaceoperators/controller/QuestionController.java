package com.spaceoperators.controller;

import com.spaceoperators.model.EQuestion;
import com.spaceoperators.payload.responses.GetQuestionResponseDTO;
import com.spaceoperators.repository.EQuestionRepository;
import com.spaceoperators.service.AIFormatterService;
import com.spaceoperators.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void save(@RequestBody EQuestion eQuestion) {
        System.out.println("QUESTION ORIGINALE : " + eQuestion.getQuestion());
        System.out.println("Question reçue : " + eQuestion);

        // Formater la question via IA
        String formattedQuestion = aiFormatterService.formatQuestionWithGroq(eQuestion.getQuestion());

        System.out.println("QUESTION FORMATTÉE : " + formattedQuestion);

        eQuestion.setQuestion(formattedQuestion);

        eQuestionRepository.save(eQuestion);
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

    @PutMapping("/edit/{id}")
    public void update0(@PathVariable Long id, @RequestBody EQuestion eQuestion) {
        EQuestion existing = eQuestionRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setQuestion(eQuestion.getQuestion());
            eQuestionRepository.save(existing);
        }
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
    public void update(@PathVariable Long id, @RequestBody EQuestion question) {
        System.out.println("Question update");

        question.setId(id); // forcer la bonne ID
        eQuestionRepository.save(question);
    }


}
