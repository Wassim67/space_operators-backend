package com.spaceoperators.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @Column(name = "id_task")
    private Integer idTask;

    @Column(name = "response")
    private String response;

    @Column(name = "type")
    private String type;

    // Getters/setters
    public Integer getIdTask() { return idTask; }
    public void setIdTask(Integer idTask) { this.idTask = idTask; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
