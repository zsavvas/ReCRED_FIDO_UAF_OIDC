package com.verizon.iam.openam;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponse {
	@JsonProperty("username_id")
	private String userId;
	@JsonProperty("last_auth_timestamp")
	private Long timestamp;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}
