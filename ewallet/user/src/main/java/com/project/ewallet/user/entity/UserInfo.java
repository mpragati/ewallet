package com.project.ewallet.user.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

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
@Table(name = "user_info", indexes = {@Index(name="uniqueEmail", columnList="email", unique=true)})
@JsonIgnoreProperties(value = {"dob", "contactNumber", "createdAt", "updatedAt"})
public class UserInfo implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String email;
	private String name;
	private String dob;
	private String contactNumber;
	
	@CreationTimestamp
	private OffsetDateTime createdAt;
	@UpdateTimestamp
	private OffsetDateTime updatedAt;

}
