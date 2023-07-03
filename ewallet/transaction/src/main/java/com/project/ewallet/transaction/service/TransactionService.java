package com.project.ewallet.transaction.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.ewallet.transaction.entity.Transaction;
import com.project.ewallet.transaction.entity.TransactionStatus;
import com.project.ewallet.transaction.exception.TransactionNotFoundException;
import com.project.ewallet.transaction.models.NewWalletRequest;
import com.project.ewallet.transaction.models.TransientTransaction;
import com.project.ewallet.transaction.repository.TransactionRepository;

@Service
@Transactional(rollbackFor = Exception.class)
public class TransactionService {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Value("${pocketbook.user.topup}")
	Long topUpDefaultAmount;
	
	@Value("${pocketbook.user.systemId}")
	Long systemId;
	
	public Transaction createNewPendingSystemTransaction(NewWalletRequest walletRequest) {	
		Transaction transaction = walletRequest.toTransaction();
		transaction.setAmount(Double.valueOf(topUpDefaultAmount));
		transaction.setSenderId(systemId);
		return saveOrUpdate(transaction);
	}

	private Transaction saveOrUpdate(Transaction transaction) {
		return transactionRepository.save(transaction);
	}

	public Transaction markTransaction(TransientTransaction transientTransaction, TransactionStatus status) {
		Optional<Transaction> byId = transactionRepository.findById(transientTransaction.getId());
		if(byId.isEmpty()) {
			throw new TransactionNotFoundException(); 
		}
		
		Transaction persisted = byId.get();
		persisted.setStatus(status);
		return saveOrUpdate(persisted);
	}
}
