package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util;

import static org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.PregnancyGapService.QR_CODE_GAP_CARE_PNG;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;

import org.sidindonesia.bidanreport.config.property.QRCodeProperties;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.web.response.FileUploadResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Transactional(readOnly = true)
@Service
public class UploadFileService {
	private final QontakProperties qontakProperties;
	private final WebClient webClient;
	private final Gson gson;
	private final QRCodeWriter qrCodeWriter;
	private final QRCodeProperties qrCodeProperties;
	private final Path qrCodeFilePath;
	private final FileSystemResource qrCodeFileSystemResource;

	public UploadFileService(QontakProperties qontakProperties, WebClient webClient, Gson gson,
		QRCodeWriter qrCodeWriter, QRCodeProperties qrCodeProperties) {
		super();
		this.qontakProperties = qontakProperties;
		this.webClient = webClient;
		this.gson = gson;
		this.qrCodeWriter = qrCodeWriter;
		this.qrCodeProperties = qrCodeProperties;
		this.qrCodeFilePath = FileSystems.getDefault()
			.getPath(qrCodeProperties.getDirectoryPath() + QR_CODE_GAP_CARE_PNG);
		this.qrCodeFileSystemResource = new FileSystemResource(qrCodeFilePath);
	}

	public FileUploadResponse uploadQRCodeImageToQontak(String contents) {
		try {
			BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, qrCodeProperties.getWidth(),
				qrCodeProperties.getHeight());
			MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCodeFilePath);

			Mono<FileUploadResponse> response = webClient.post().uri(qontakProperties.getApiPathUploadFile())
				.body(BodyInserters.fromMultipartData("file", qrCodeFileSystemResource))
				.header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
				.bodyToMono(FileUploadResponse.class).onErrorResume(WebClientResponseException.class,
					ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
						? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), FileUploadResponse.class))
						: Mono.error(ex));

			return response.block();
		} catch (WriterException | IOException e) {
			log.warn(Arrays.toString(e.getStackTrace()));
		}
		return null;
	}
}
