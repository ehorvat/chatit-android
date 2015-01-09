package com.ego.interfaces;

import com.ego.exceptions.InvalidEmailException;
import com.ego.exceptions.PasswordMismatchException;

public interface Checks {
	public void check() throws PasswordMismatchException, InvalidEmailException;
	public boolean checkPasswords();
	public boolean checkEmail();
}
