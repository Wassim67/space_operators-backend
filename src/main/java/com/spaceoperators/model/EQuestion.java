package com.spaceoperators.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "QUESTIONS")
public class EQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @Column(name = "options", nullable = false, columnDefinition = "TEXT")
    private String optionsJson;

    @Transient
    private List<String> options;

    @Column(name = "correct_option_index", nullable = false)
    private String correctOptionIndex;

    private static final ObjectMapper mapper = new ObjectMapper();

    // Getters / setters classiques

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
        if (options == null && optionsJson != null) {
            try {
                options = mapper.readValue(optionsJson, new TypeReference<List<String>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
        try {
            this.optionsJson = mapper.writeValueAsString(options);
        } catch (Exception e) {
            e.printStackTrace();
            this.optionsJson = null;
        }
    }

    public String getOptionsJson() {
        return optionsJson;
    }

    public void setOptionsJson(String optionsJson) {
        this.optionsJson = optionsJson;
        // Invalidate transient list to force reload if needed
        this.options = null;
    }

    public String getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(String correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }
}
