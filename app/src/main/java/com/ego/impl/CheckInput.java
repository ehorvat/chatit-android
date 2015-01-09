package com.ego.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ego.exceptions.InvalidEmailException;
import com.ego.exceptions.PasswordMismatchException;
import com.ego.interfaces.Checks;

/////////////////////////////////////////////////////
//
// Class for verifying registration and login input
//
/////////////////////////////////////////////////////

public class CheckInput implements Checks{
	
	private String email, password, confPassword;
	
	private Pattern pattern;
	
	private Matcher matcher;
 
	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public CheckInput(String email, String password, String confPassword){
		this.email=email;
		this.password=password;
		this.confPassword=confPassword;
		pattern = Pattern.compile(EMAIL_PATTERN);
	}
	
	@Override
	public void check() throws PasswordMismatchException, InvalidEmailException {
				
		if(!checkPasswords()){
			throw new PasswordMismatchException("Passwords don't match");
		}
		
		if(!checkEmail()){
			throw new InvalidEmailException("Invalid Email");
		}
		
	}
	
	public boolean checkPasswords(){
		boolean good = true;
		if(!password.trim().equals(confPassword.trim())){
			good=false;
		}
		return good;
	}
	
	public boolean checkEmail(){
		matcher = pattern.matcher(email.trim());
		return matcher.matches();
	}
	

}
