package com.verizon.iam.openam;


import com.fasterxml.jackson.annotation.JsonProperty;

public class FidoAuthenticationResponse {
	@JsonProperty("authenticated")
	private Boolean localAuthenticated;
	@JsonProperty("username")
	private String localUsername;
	
	public String getUsername() {
		return localUsername;
	}
	public void setUsername(String inUsername) {
		this.localUsername = inUsername;
	}
	public Boolean isAuthenticated() {
		return this.localAuthenticated;
	}
	public void setAuthenticated(Boolean inAuth) {
		this.localAuthenticated = inAuth;
	}
	
}
