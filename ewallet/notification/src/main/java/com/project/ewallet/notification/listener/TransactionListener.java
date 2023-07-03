package com.project.ewallet.notification.listener;

import java.util.Objects; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.project.ewallet.notification.models.NotificationUser;
import com.project.ewallet.notification.models.Transaction;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TransactionListener {
	
	private static final String TRANSACTION_SUCCESS = "TRANSACTION_SUCCESS";

	private static final String TRANSACTION_FAILURE = "TRANSACTION_FAILURE";
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	JavaMailSender javaMailSender;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	Gson gson;
	
	@Value("${pocketbook.user.systemId}")
	Long systemId;
	
	@Value("${spring.mail.username}")
	String SystemUser;
	
	@SneakyThrows
	@KafkaListener(topics = {TRANSACTION_SUCCESS}, groupId = "notification_group")
	public void sendSuccessMessage(@Payload String message) {
		
		log.info("*********** TRANSACTION SUCCESS LISTENER :: start ***********");
		/**
		 * sender Id = do not have email
		 * receiver Id = do not have email
		 * 
		 */
		
		Transaction transaction = objectMapper.readValue(message, Transaction.class);
		
		/**
		 * Fetch sender email
		 * Fetch receiver email
		 */
		NotificationUser sender = fetchUserById(transaction.getSenderId());
		NotificationUser receiver = fetchUserById(transaction.getReceiverId());
		
		if(!Objects.equals(sender.getId(), systemId)) {
			log.info("*********** TRANSACTION SUCCESS LISTENER :: SENDING MESSAGE TO SENDER ***********");
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setFrom(SystemUser);
			simpleMailMessage.setTo(sender.getEmail());
			simpleMailMessage.setText(" Hey ! " + sender.getName() + " Your account has been debited by " + transaction.getAmount());
			simpleMailMessage.setSubject("Amount Debited");
			javaMailSender.send(simpleMailMessage);
			
		}
		
		if(!Objects.equals(receiver.getId(), systemId)) {
			log.info("*********** TRANSACTION SUCCESS LISTENER :: SENDING MESSAGE TO RECEIVER ***********");
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setFrom(SystemUser);
			simpleMailMessage.setTo(receiver.getEmail());
			simpleMailMessage.setText(" Hey ! " + receiver.getName() + " Your account has been credited by " + transaction.getAmount());
			simpleMailMessage.setSubject("Amount Credited");
			javaMailSender.send(simpleMailMessage);
			
		}
		log.info("*********** TRANSACTION SUCCESS LISTENER :: end ***********");
		
	}
	
	@SneakyThrows
	@KafkaListener(topics = {TRANSACTION_FAILURE}, groupId = "notification_group")
	public void sendFailureMessage(@Payload String message) {
		
		log.info("*********** TRANSACTION FAILURE LISTENER :: START ***********");
		
		Transaction transaction = objectMapper.readValue(message, Transaction.class);
		NotificationUser sender = fetchUserById(transaction.getSenderId());
		
		if(!Objects.equals(sender.getId(), systemId)) {
			log.info("*********** TRANSACTION FAILURE LISTENER :: SENDING MESSAGE TO SENDER ***********");
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setFrom(SystemUser);
			simpleMailMessage.setTo(sender.getEmail());
			simpleMailMessage.setText(" Hey ! " + sender.getName() + " Your recent transaction for " + transaction.getAmount() + " has failed !");
			simpleMailMessage.setSubject("Transaction failed");
			javaMailSender.send(simpleMailMessage);
			
		}
		log.info("*********** TRANSACTION FAILURE LISTENER :: END ***********");

	}


	private NotificationUser fetchUserById(Long id) {
		if(Objects.equals(id, systemId)) {
			NotificationUser sender = new NotificationUser();
			sender.setId(systemId);
			return sender;
		}
		ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:9095/user/" + id, String.class);
		return gson.fromJson(forEntity.getBody(), NotificationUser.class);
	}

}
