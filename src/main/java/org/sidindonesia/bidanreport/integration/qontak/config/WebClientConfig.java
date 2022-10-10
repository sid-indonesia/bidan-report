package org.sidindonesia.bidanreport.integration.qontak.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
	@Primary
	@Bean
	public WebClient qontakWhatsAppWebClient(@Value("${qontak.base-url}") String baseUrl,
		@Value("${qontak.response-timeout-in-ms}") Long responseTimeout,
		@Value("${qontak.connection-timeout-in-ms}") Long connectionTimeout) {
		HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(responseTimeout)).baseUrl(baseUrl)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout.intValue())
			.option(ChannelOption.SO_KEEPALIVE, true).option(EpollChannelOption.TCP_KEEPIDLE, 300)
			.option(EpollChannelOption.TCP_KEEPINTVL, 60).option(EpollChannelOption.TCP_KEEPCNT, 8);

		return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
	}

	@Bean
	public Gson defaultGson() {
		return new Gson();
	}
}
