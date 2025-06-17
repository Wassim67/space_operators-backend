package com.spaceoperators.service;

import com.spaceoperators.model.EQuestion;
import com.spaceoperators.payload.responses.GetQuestionResponseDTO;
import com.spaceoperators.repositories.EQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    private EQuestionRepository eQuestionRepository;

    public QuestionService(EQuestionRepository eQuestionRepository) {
        this.eQuestionRepository = eQuestionRepository;
    }


    public List<GetQuestionResponseDTO> get() {
        List<EQuestion> entityQuestions = eQuestionRepository.findAll();
        List<GetQuestionResponseDTO> dtoQuestions = new ArrayList<GetQuestionResponseDTO>();

        entityQuestions.forEach((entity) -> {
            GetQuestionResponseDTO dto = new GetQuestionResponseDTO();
            dto.setId(entity.getId());
            dto.setQuestion(entity.getQuestion());


            dtoQuestions.add(dto);
        });
        return dtoQuestions;
    }

}
