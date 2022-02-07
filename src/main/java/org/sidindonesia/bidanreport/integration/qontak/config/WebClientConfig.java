package org.sidindonesia.bidanreport.integration.qontak.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
public class WebClientConfig {
	@Primary
	@Bean
	public WebClient qontakWhatsAppWebClient(@Value("${qontak.base-url}") String baseUrl) {
		return WebClient.create(baseUrl);
	}

	@Primary
	@Bean
	public Gson defaultGson() {
		return new Gson();
	}

	@Bean
	public Gson prettyGson() {
		return new GsonBuilder().setPrettyPrinting().create();
	}
}
