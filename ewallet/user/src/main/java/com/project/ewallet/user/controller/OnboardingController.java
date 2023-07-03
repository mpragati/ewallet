package com.project.ewallet.user.controller;

import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.user.entity.UserInfo;
import com.project.ewallet.user.request.CreateUserRequestDto;
import com.project.ewallet.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class OnboardingController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	ObjectMapper objectMapper;
	
	
	@PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createAnUser(@Valid @RequestBody CreateUserRequestDto requestDto) throws JsonProcessingException{
		UserInfo newUser = userService.createNewUser(requestDto);
		userService.sendMessage(newUser);
		return new ResponseEntity<>(objectMapper.writeValueAsString(newUser), HttpStatus.CREATED);
	}
	
	@GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getUser(@PathVariable(value = "id") Long id) throws JsonProcessingException{
		UserInfo newUser = userService.fetchUserById(id);
		return new ResponseEntity<>(objectMapper.writeValueAsString(newUser), HttpStatus.CREATED);
	}

}
