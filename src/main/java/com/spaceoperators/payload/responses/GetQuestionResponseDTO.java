package com.spaceoperators.payload.responses;

import java.util.List;

public class GetQuestionResponseDTO {

    private Long id;

    private String question;

    private List<String> options;

    private String correctOptionIndex;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(String correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }
}