package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util;

import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.web.response.FileUploadResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UploadFileService {
	private final QontakProperties qontakProperties;
	private final WebClient webClient;
	private final Gson gson;

	public FileUploadResponse uploadFileToQontak(FileSystemResource fileSystemResource) {
		Mono<FileUploadResponse> response = webClient.post().uri(qontakProperties.getApiPathUploadFile())
			.body(BodyInserters.fromMultipartData("file", fileSystemResource))
			.header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
			.bodyToMono(FileUploadResponse.class).onErrorResume(WebClientResponseException.class,
				ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
					? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), FileUploadResponse.class))
					: Mono.error(ex));

		return response.block();
	}
}
