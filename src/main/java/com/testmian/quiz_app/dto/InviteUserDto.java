package com.testmian.quiz_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class InviteUserDto {
	
	 @NotBlank(message = "Full name is required")
	    private String fullName;

	    @Email(message = "Invalid email format")
	    @NotBlank(message = "Email is required")
	    private String email;
	    

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

	    
	  

}
