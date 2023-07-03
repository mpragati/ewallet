package com.project.ewallet.transaction.models;

import com.project.ewallet.transaction.entity.Transaction;

import lombok.Data;

@Data
public class NewWalletRequest {
	
	Long id;
	Long userId;
	
	public Transaction toTransaction() {
		return Transaction.builder()
				.receiverId(userId)
				.build();
	}

}
