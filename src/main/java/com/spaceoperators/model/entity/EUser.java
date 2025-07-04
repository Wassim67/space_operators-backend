//package com.spaceoperators.model.entity;
//
//import jakarta.persistence.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "PLAYER")
//public class EUser {
//
//	@Id
//	//@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private String playerId;
//
//	private String username;
//
//	private String password;
//
//	//@ManyToMany(cascade = CascadeType.ALL)
//	//private List<ERole> roles = new ArrayList<ERole>();
//
////	@ManyToOne
////	@JoinColumn(name = "idCatecory")
////	private ECategory category;
//
//	public String getId() {
//		return playerId;
//	}
//
//	public void setId(String playerId) {
//		this.playerId = playerId;
//	}
//
//	public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
////	public List<ERole> getRoles() {
////		return roles;
////	}
////
////	public void addRole(ERole role) {
////		this.roles.add(role);
////	}
////
////	public void removeRole(ERole role) {
////		this.roles.remove(role);
////	}
////
////	public ECategory getCategory() {
////		return category;
////	}
////
////	public void setCategory(ECategory category) {
////		this.category = category;
////	}
//}
