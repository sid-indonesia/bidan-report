package org.sidindonesia.bidanreport.integration.qontak.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

@Configuration
public class WebClientConfig {
	@Primary
	@Bean
	public WebClient qontakWhatsAppWebClient(@Value("${qontak.base-url}") String baseUrl) {
		return WebClient.create(baseUrl);
	}

	@Bean
	public Gson defaultGson() {
		return new Gson();
	}
}
