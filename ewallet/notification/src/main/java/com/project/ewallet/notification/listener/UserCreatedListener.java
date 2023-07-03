package com.project.ewallet.notification.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.notification.models.NotificationUser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserCreatedListener {
	
	private static final String USER_CREATED = "USER_CREATED";
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	String SystemUser;

	@SneakyThrows
	@KafkaListener(topics = {USER_CREATED}, groupId = "notification_group")
	public void receiveMessage(@Payload String message) {
		
		/**
		 * send an email to user that his account has been created.
		 */
		
		log.info("***************** SENDING EMAIL TO NEW USER CREATION : START *****************");
		
		NotificationUser notificationUser = objectMapper.readValue(message, NotificationUser.class);
		
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(notificationUser.getEmail());
		simpleMailMessage.setFrom(SystemUser);
		simpleMailMessage.setSubject("Welcome Onboard!!");
		simpleMailMessage.setText("Hey "+notificationUser.getName()+" Your account has been successfully created with us!! "+
				""+
				""+
				"We will be crediting some balance in a short span for enrolling in our service. Thanks!");
		javaMailSender.send(simpleMailMessage);
		
		log.info("***************** SENDING EMAIL TO NEW USER CREATION : START *****************");
		
	}

}
