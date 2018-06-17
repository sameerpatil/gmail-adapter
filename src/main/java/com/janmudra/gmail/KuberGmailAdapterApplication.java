package com.janmudra.gmail;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.google.api.services.gmail.Gmail;

@SpringBootApplication(scanBasePackages = {"com.janmudra.gmail*"})
@EnableScheduling
public class KuberGmailAdapterApplication {
	
	@Autowired 
	GmailInitializer initializer;
	public static void main(String[] args) {
		SpringApplication.run(KuberGmailAdapterApplication.class, args);
	
	}

	@Bean
	public Gmail initGmail() {
		Gmail gmail = null;
		try {
			gmail = initializer.initGmailService();
		} catch (IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gmail;
	}
}
