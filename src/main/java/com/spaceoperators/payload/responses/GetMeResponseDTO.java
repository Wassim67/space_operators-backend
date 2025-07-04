package com.spaceoperators.payload.responses;

import java.util.ArrayList;
import java.util.List;

public class GetMeResponseDTO {
    private String username;

    private List<String> roles = new ArrayList<String>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}