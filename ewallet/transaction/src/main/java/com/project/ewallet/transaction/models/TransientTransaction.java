package com.project.ewallet.transaction.models;

import lombok.Data;

@Data
public class TransientTransaction {

	Long senderId;
	Long receiverId;
	Double amount;
	
	//transaction id
	Long id; 
}
