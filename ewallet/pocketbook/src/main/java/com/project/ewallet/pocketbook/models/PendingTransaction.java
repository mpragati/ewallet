package com.project.ewallet.pocketbook.models;

import lombok.Data;

@Data
public class PendingTransaction {
	
	Long senderId;
	Long receiverId;
	Double amount;
	
	//transaction id
	Long id; 

}
