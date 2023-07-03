package com.project.ewallet.transaction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ApplicationConfiguration {

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
