package com.project.ewallet.transaction.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@JsonIgnoreProperties(value = {"status", "createdAt", "updatedAt"})
public class Transaction implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@CreationTimestamp
	private OffsetDateTime createdAt;
	@UpdateTimestamp
	private OffsetDateTime updatedAt;
	
	@Enumerated(value = EnumType.STRING)
	private TransactionStatus status;
	
	private Double amount;
	private Long senderId;
	private Long receiverId;
	
	@PrePersist
	public void defaultTransactionUpdate() {
		this.status=TransactionStatus.PENDING;
	}

}
