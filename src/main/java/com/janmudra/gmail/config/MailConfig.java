package com.janmudra.gmail.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {
	
	@Value("${userId}")
	private String userId;
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Value("${kuberLabel}")
    private String kuberLabel;

	public String getKuberLabel() {
		return kuberLabel;
	}

	public void setKuberLabel(String kuberLabel) {
		this.kuberLabel = kuberLabel;
	}


}
