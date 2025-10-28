package com.testmian.quiz_app.dto;

public class AcceptInviteResponseDTO {
    private String message;
    private String status;
    private String email;
    private String role;
    private String token;

    public AcceptInviteResponseDTO(String message, String status, String email, String role, String token) {
        this.message = message;
        this.status = status;
        this.email = email;
        this.role = role;
        this.token = token;
    }

	public AcceptInviteResponseDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

  
 
}
