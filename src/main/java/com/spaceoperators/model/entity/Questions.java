package com.spaceoperators.model.entity;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "questions")
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "question", nullable = false)
    private String question;

    // Stockage JSON des options en base, type TEXT
    @Column(name = "options", nullable = false, columnDefinition = "TEXT")
    private String optionsJson;

    // Non persistant, mappé à optionsJson via Jackson
    @Transient
    private List<String> options;

    @Column(name = "correct_option_index", nullable = false)
    private String correctOptionIndex;

    private static final ObjectMapper mapper = new ObjectMapper();

    // Getter pour options qui dé-sérialise optionsJson au besoin
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

    // Setter pour options qui sérialise en JSON dans optionsJson
    public void setOptions(List<String> options) {
        this.options = options;
        try {
            this.optionsJson = mapper.writeValueAsString(options);
        } catch (Exception e) {
            e.printStackTrace();
            this.optionsJson = null;
        }
    }

    // Getters/setters classiques
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }

    public String getCorrectOptionIndex() {
        return correctOptionIndex;
    }
    public void setCorrectOptionIndex(String correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }}
