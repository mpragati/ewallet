package com.project.ewallet.pocketbook.service;

import java.util.Objects;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.project.ewallet.pocketbook.entity.Wallet;
import com.project.ewallet.pocketbook.exception.WalletExistsException;
import com.project.ewallet.pocketbook.models.PendingTransaction;
import com.project.ewallet.pocketbook.models.PocketBookUser;
import com.project.ewallet.pocketbook.repository.WalletRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class WalletService {
	
	@Autowired
	WalletRepository walletRepository;
	
	@Value("${pocketbook.user.systemId}")
	Long systemId;
	
	public Wallet createNewWallet(PocketBookUser user) {
		Optional<Wallet> wallet = walletRepository.findByUserId(user.getId());
		if(wallet.isPresent()) {
			throw new WalletExistsException();
		}
		return saveOrUpdate(user.toWallet());
	}
	
	private Wallet saveOrUpdate(Wallet wallet) {
		return walletRepository.save(wallet);
	}
	
	public void topUpWallets(PendingTransaction pendingTransaction) {
		/**
		 * sender wallet 
		 * 		- check if there are sufficient funds
		 * receiver wallet
		 * 
		 * reduce amount and done
		 * 
		 */
		log.info(" received {}", pendingTransaction);
		if(!Objects.equals(pendingTransaction.getSenderId(), systemId)) {
			Optional<Wallet> senderWallet = walletRepository.findByUserId(pendingTransaction.getSenderId());
			if(senderWallet.isEmpty()) {
				log.error("Sender wallet is not present");
				throw new RuntimeException();
			}
			
			if(senderWallet.get().getBalance() < pendingTransaction.getAmount()) {
				//insufficient balance
				log.error("Sender wallet has insufficient balance");
				throw new RuntimeException();
			}
			// Now top up
			Wallet sender=senderWallet.get();
			sender.setBalance(Double.sum(sender.getBalance(), -1*pendingTransaction.getAmount()));
			saveOrUpdate(sender);

		}
		
		
		if(!Objects.equals(pendingTransaction.getReceiverId(), systemId)) {
		Optional<Wallet> receiverWallet = walletRepository.findByUserId(pendingTransaction.getReceiverId());
		if(receiverWallet.isEmpty()) {
			log.error("Receiver wallet is not present");
			throw new RuntimeException();
		}	
		// Now top up
		Wallet receiver=receiverWallet.get();			
		if(!Objects.equals(receiver.getId(), systemId)) {
			receiver.setBalance(Double.sum(receiver.getBalance(), pendingTransaction.getAmount()));
		}	
		saveOrUpdate(receiver);
	}
	}

}
