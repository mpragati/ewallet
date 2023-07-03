package com.project.ewallet.notification.models;

import lombok.Data;

@Data
public class Transaction {
	
	//transaction id
	Long id; 
	
	Long senderId;
	Long receiverId;
	Double amount;

}
