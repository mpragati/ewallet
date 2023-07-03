package com.project.ewallet.pocketbook.listener;

import static com.project.ewallet.pocketbook.utils.KafkaMessageLogger.addCallBack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.pocketbook.entity.Wallet;
import com.project.ewallet.pocketbook.exception.WalletExistsException;
import com.project.ewallet.pocketbook.models.PocketBookUser;
import com.project.ewallet.pocketbook.service.WalletService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserCreationListener {
	
	private static final String USER_CREATED = "USER_CREATED";
	
	private static final String NEW_WALLET_CREATED = "NEW_WALLET_CREATED";

	
	@Autowired
	ObjectMapper objectMapper;
	
	
	@Autowired
	WalletService walletService;
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@SneakyThrows
	@KafkaListener(topics = {USER_CREATED}, groupId = "pocketbook_group")
	public void receiveMessage(@Payload String message) {
		log.info("*********** USER CREATED LISTENER :: start ***********");
		/**
		 * send an email to user that his account has been created.
		 */
		PocketBookUser user = objectMapper.readValue(message, PocketBookUser.class);
		
		try {
			Wallet newWallet = walletService.createNewWallet(user);
			String messageOutbox = objectMapper.writeValueAsString(newWallet);
			addCallBack(messageOutbox, kafkaTemplate.send(NEW_WALLET_CREATED, messageOutbox));
		} catch (WalletExistsException exception) {
			/**
			 * do nothing as wallet already exists.
			 */
			log.info("********* WALLET ALREADY EXISTS *********");
			
		}
		log.info("*********** USER CREATED LISTENER :: end ***********");
	}

}
