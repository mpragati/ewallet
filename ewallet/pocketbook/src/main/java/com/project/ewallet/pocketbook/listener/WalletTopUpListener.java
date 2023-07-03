package com.project.ewallet.pocketbook.listener;

import static com.project.ewallet.pocketbook.utils.KafkaMessageLogger.addCallBack;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.pocketbook.exception.WalletExistsException;
import com.project.ewallet.pocketbook.models.PendingTransaction;
import com.project.ewallet.pocketbook.service.WalletService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WalletTopUpListener {
	
	private static final String TOPUP_WALLET = "TOPUP_WALLET";

	private static final String TOPUP_SUCCESS = "TOPUP_SUCCESS";

	private static final String TOPUP_FAILURE = "TOPUP_FAILURE";

	@Autowired
	ObjectMapper objectMapper;
	
	
	@Autowired
	WalletService walletService;
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@SneakyThrows
	@KafkaListener(topics = {TOPUP_WALLET}, groupId = "pocketbook_group")
	public void receiveMessage(@Payload String message) {
			log.info("******** TOPUP WALLET LISTENER :: START ********");
			/**
			 * send an email to user that his account has been created.
			 */
			PendingTransaction transaction= objectMapper.readValue(message, PendingTransaction.class);
			String messageOutbox = objectMapper.writeValueAsString(transaction);
			ListenableFuture<SendResult<String, String>> send = null;
			
			try {
				walletService.topUpWallets(transaction);
				send = kafkaTemplate.send(TOPUP_SUCCESS, messageOutbox);	
				
			} catch (WalletExistsException exception) {
				log.info("******** TOPUP WALLET LISTENER :: INSIDE CATCH EXCEPTION ********");
				send = kafkaTemplate.send(TOPUP_FAILURE, messageOutbox);
				
			} finally {
				log.info("******** TOPUP WALLET LISTENER :: INSIDE FINALLY ********");
				assert send != null;
				addCallBack(messageOutbox, send);
			}
			
			log.info("******** TOPUP WALLET LISTENER :: END ********");
		}

}
