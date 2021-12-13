package org.sidindonesia.bidanreport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.zxing.qrcode.QRCodeWriter;

@Configuration
@EnableJpaRepositories({ "org.sidindonesia.bidanreport.repository",
		"org.sidindonesia.bidanreport.integration.qontak.repository" })
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfiguration {

	@Bean
	public QRCodeWriter qrCodeWriter() {
		return new QRCodeWriter();
	}
}
