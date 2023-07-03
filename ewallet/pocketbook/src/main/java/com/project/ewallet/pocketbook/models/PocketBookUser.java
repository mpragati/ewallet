package com.project.ewallet.pocketbook.models;

import com.project.ewallet.pocketbook.entity.Wallet;

import lombok.Data;

@Data
public class PocketBookUser {
	
	private Long id;
	
	private String email;
	private String name;
	
	public Wallet toWallet() {
		return Wallet.builder()
				.userId(id)
				.balance((double)0)
				.build();
	}

}
