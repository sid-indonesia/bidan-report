package org.sidindonesia.bidanreport.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "qr-code")
public class QRCodeProperties {
	private int width;
	private int height;
	private String directoryPath;
}
