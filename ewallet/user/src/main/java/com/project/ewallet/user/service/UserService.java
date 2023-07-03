package com.project.ewallet.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.user.entity.UserInfo;
import com.project.ewallet.user.exceptions.DuplicateUserException;
import com.project.ewallet.user.exceptions.NoUserFoundException;
import com.project.ewallet.user.repository.UserInfoRepository;
import com.project.ewallet.user.request.CreateUserRequestDto;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	
	@Autowired
	UserInfoRepository repository;
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;
	
	private static final String USER_CREATED = "USER_CREATED";
	
	@Autowired
	ObjectMapper objectMapper;
	
	
	@Transactional(rollbackFor = Exception.class)
	public UserInfo createNewUser(CreateUserRequestDto requestDto) {
		UserInfo transientUserInfo = requestDto.toUserInfo();
		Optional<UserInfo> byEmail = repository.findByEmail(transientUserInfo.getEmail());
		if(byEmail.isPresent()) {
			throw new DuplicateUserException();
		}
		return saveOrUpdate(transientUserInfo);
				
	}
	
	@SneakyThrows
	public void sendMessage(UserInfo userInfo) {
		
		String message = objectMapper.writeValueAsString(userInfo);
		addCallBack(message, kafkaTemplate.send(USER_CREATED, message));

	}
	
	public void addCallBack(String message, ListenableFuture<SendResult<String, String>> send) {
		
		send.addCallback(new ListenableFutureCallback<>() {

			@Override
			public void onFailure(Throwable ex) { log.info(" ****** unable to send message=[" + message + "]");	}
			
			@Override
			public void onSuccess(SendResult<String, String> result) {
				
				log.info(" **** sent message=[" + "] with " +
				" partition=[" + result.getRecordMetadata().partition() + "]" +
				" offset=[" + result.getRecordMetadata().offset() + "]");
			}	
		});
		
	}
	
	private UserInfo saveOrUpdate(UserInfo userInfo) {
		return repository.save(userInfo);
	}

	public UserInfo fetchUserById(Long id) {
		Optional<UserInfo> byId = repository.findById(id);
		if(byId.isEmpty()) {
			throw new NoUserFoundException();
		}
		return byId.get();
	}
}
