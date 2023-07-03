package com.project.ewallet.transaction.listener;

import static com.project.ewallet.transaction.utils.KafkaMessageLogger.addCallBack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.transaction.entity.Transaction;
import com.project.ewallet.transaction.models.NewWalletRequest;
import com.project.ewallet.transaction.service.TransactionService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WalletCreatedListener {
	
	private static final String NEW_WALLET_CREATED = "NEW_WALLET_CREATED";
	
	private static final String TOPUP_WALLET = "TOPUP_WALLET";

	
	@Autowired
	ObjectMapper objectMapper;
	
	
	@Autowired
	TransactionService service;
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@SneakyThrows
	@KafkaListener(topics = {NEW_WALLET_CREATED}, groupId = "transaction_group")
	public void processTransaction(@Payload String message) {
		
		log.info("*********** NEW WALLET CREATED LISTENER :: start ***********");
		/**
		 * create a transaction in pending state
		 * 		- topup wallet
		 * 			-mark transaction success
		 */
		
		NewWalletRequest newWalletRequest = objectMapper.readValue(message, NewWalletRequest.class);
		Transaction newPendingSystemTransaction = service.createNewPendingSystemTransaction(newWalletRequest);
		String messageOutbox = objectMapper.writeValueAsString(newPendingSystemTransaction);
		addCallBack(messageOutbox, kafkaTemplate.send(TOPUP_WALLET, messageOutbox));
		
		log.info("*********** NEW WALLET CREATED LISTENER :: end ***********");

	}

}
