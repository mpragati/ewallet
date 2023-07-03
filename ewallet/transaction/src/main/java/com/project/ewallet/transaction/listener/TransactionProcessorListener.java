package com.project.ewallet.transaction.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.transaction.entity.Transaction;
import com.project.ewallet.transaction.entity.TransactionStatus;
import com.project.ewallet.transaction.models.TransientTransaction;
import com.project.ewallet.transaction.service.TransactionService;

import static com.project.ewallet.transaction.utils.KafkaMessageLogger.addCallBack;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TransactionProcessorListener {
	
	private static final String TOPUP_SUCCESS = "TOPUP_SUCCESS";

	private static final String TOPUP_FAILURE = "TOPUP_FAILURE";
	
	private static final String TRANSACTION_SUCCESS = "TRANSACTION_SUCCESS";

	private static final String TRANSACTION_FAILURE = "TRANSACTION_FAILURE";


	@Autowired
	ObjectMapper objectMapper;
	
	
	@Autowired
	TransactionService service;
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@SneakyThrows
	@KafkaListener(topics = {TOPUP_SUCCESS}, groupId = "transaction_group")
	public void processSuccessTransaction(@Payload String message) {
		
		log.info("*********** TOPUP SUCCESS LISTENER :: start ***********");
		/**
		 * 			-mark transaction success
		 */
		
		TransientTransaction transientTransaction = objectMapper.readValue(message, TransientTransaction.class);
		Transaction transaction = service.markTransaction(transientTransaction, TransactionStatus.SUCCESS);
		String messageOutbox = objectMapper.writeValueAsString(transaction);
		addCallBack(messageOutbox, kafkaTemplate.send(TRANSACTION_SUCCESS, messageOutbox));
		
		log.info("*********** TOPUP SUCCESS LISTENER :: end ***********");
	}

	@SneakyThrows
	@KafkaListener(topics = {TOPUP_FAILURE}, groupId = "transaction_group")
	public void processFailedTransaction(@Payload String message) {
		
		log.info("*********** TOPUP FAILURE LISTENER :: start ***********");
		/**
		 * 			-mark transaction failed
		 */
		
		TransientTransaction transientTransaction = objectMapper.readValue(message, TransientTransaction.class);
		Transaction transaction = service.markTransaction(transientTransaction, TransactionStatus.FAILURE);
		String messageOutbox = objectMapper.writeValueAsString(transaction);
		addCallBack(messageOutbox, kafkaTemplate.send(TRANSACTION_FAILURE, messageOutbox));
		
		log.info("*********** TOPUP FAILURE LISTENER :: end ***********");
	}

}
