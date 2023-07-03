package com.project.ewallet.user.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.ewallet.user.exceptions.DuplicateUserException;
import com.project.ewallet.user.exceptions.NoUserFoundException;

@RestControllerAdvice
public class GlobalControllerAdvice {
	
	@ExceptionHandler(value = {DuplicateUserException.class, NoUserFoundException.class})
	public ResponseEntity<String> generateDuplicateUserException(){
		return new ResponseEntity<>("user already exists.", HttpStatus.BAD_REQUEST);
	}

}
