package com.spaceoperators.payload.responses;

import java.util.ArrayList;
import java.util.List;

public class GetUserByUsernameResponseDTO {
	private String id;

	private String username;
	
	private String password;

	private List<String> roles = new ArrayList<String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
