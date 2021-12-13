package org.sidindonesia.bidanreport.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.sidindonesia.bidanreport.IntegrationTest;
import org.sidindonesia.bidanreport.integration.qontak.config.property.QontakProperties;
import org.sidindonesia.bidanreport.integration.qontak.web.response.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import reactor.core.publisher.Mono;

@IntegrationTest
class QRCodeServiceTest {

	@Autowired
	private QRCodeWriter qrCodeWriter;

	@Autowired
	private WebClient webClient;

	@Autowired
	private Gson gson;

	@Autowired
	private QontakProperties qontakProperties;

	@Value("${qr-code.width}")
	private int qrCodeWidth;

	@Value("${qr-code.height}")
	private int qrCodeHeight;

	@Value("${qr-code.directory-path}")
	private String directoryPath;

	@Test
	void testUploadQRCodeToMockServer() throws Exception {
		String contents = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum at purus tempor, vehicula lorem sit amet, tristique magna. Aliquam ac neque nec odio commodo sagittis euismod eu metus. Vivamus maximus tellus vitae pharetra imperdiet. Sed molestie vel ligula et semper. Quisque massa felis, feugiat nec tortor sed, gravida ultrices eros. Nullam porta consequat scelerisque. Aenean vulputate hendrerit quam at vestibulum. Ut congue eros vel mauris semper, sit amet vulputate odio bibendum. Cras eget ligula sed justo tincidunt auctor. Pellentesque accumsan semper lacus sed ornare. Donec iaculis nisi sed dolor vehicula pretium. Nulla nec metus enim.\r\n"
			+ "\r\n"
			+ "Nunc finibus nisl ante, in ornare nisi venenatis quis. Praesent vitae enim non est tristique porta eget non nunc. Curabitur sit amet neque viverra, faucibus ligula interdum, ullamcorper velit. Donec consequat ipsum at orci sodales, sit amet accumsan nunc tincidunt. Vivamus ac varius neque, quis laoreet arcu. Nullam id lobortis orci, vitae elementum ante. Vestibulum suscipit nulla eu lectus cursus efficitur. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed tristique turpis ex, at mattis lectus posuere et. Integer lectus nisl, vehicula a lobortis ut, pellentesque a est. Etiam viverra elementum iaculis. Vestibulum convallis quam augue, nec imperdiet ex elementum vel. Aliquam lacinia convallis luctus. Donec ullamcorper lobortis metus a efficitur. Vivamus auctor, mi eu accumsan semper, orci sem finibus diam, eu fermentum augue dolor sit amet lacus. Quisque ultricies varius est in sollicitudin.\r\n"
			+ "\r\n"
			+ "Fusce porta nunc vel ex vulputate, sit amet hendrerit sem volutpat. Donec posuere porttitor tincidunt. Phasellus nisl tellus, maximus id metus non, volutpat posuere velit. Sed ipsum dolor, efficitur id erat iaculis, rhoncus ullamcorper lacus. Integer vitae ex non massa scelerisque fringilla. Vivamus vel magna et metus dictum tristique vitae nec nisl. Sed nec lectus nulla. Aenean scelerisque turpis in arcu feugiat, at dapibus nunc viverra. Nulla vestibulum est vel sem rhoncus, eu placerat lacus venenatis. Curabitur pulvinar lacinia consequat.\r\n"
			+ "\r\n"
			+ "Mauris eget velit urna. Nam mollis sapien eu gravida tincidunt. Phasellus vel nisl nulla. Cras non libero hendrerit, aliquet leo quis, vehicula nibh. Nunc porttitor consequat nibh. Nam pretium dignissim malesuada. In malesuada justo quis dui varius, sit amet posuere magna aliquam.\r\n"
			+ "\r\n"
			+ "Etiam est diam, pulvinar eu massa eget, elementum elementum magna. Donec eu mi suscipit, laoreet eros velit.";
		BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeHeight);

		Path path = FileSystems.getDefault().getPath(directoryPath + "QR_Code.png");
		FileSystemResource fileSystemResource = new FileSystemResource(path);

		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

		Mono<FileUploadResponse> response = webClient.post().uri(qontakProperties.getApiPathUploadFile())
			.body(BodyInserters.fromMultipartData("file", fileSystemResource))
			.header("Authorization", "Bearer " + qontakProperties.getAccessToken()).retrieve()
			.bodyToMono(FileUploadResponse.class).onErrorResume(WebClientResponseException.class,
				ex -> ex.getRawStatusCode() == 422 || ex.getRawStatusCode() == 401
					? Mono.just(gson.fromJson(ex.getResponseBodyAsString(), FileUploadResponse.class))
					: Mono.error(ex));

		assertThat(response.block().getStatus()).isNotEqualToIgnoringCase("error");
	}
}
