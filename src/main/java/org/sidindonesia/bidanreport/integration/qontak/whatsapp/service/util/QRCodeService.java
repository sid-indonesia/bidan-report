package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util;

import static org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.FhirResourceService.QR_CODE_GAP_CARE_PNG;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;

import org.sidindonesia.bidanreport.config.property.QRCodeProperties;
import org.sidindonesia.bidanreport.integration.qontak.web.response.FileUploadResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@Service
public class QRCodeService {
	private final QRCodeWriter qrCodeWriter;
	private final QRCodeProperties qrCodeProperties;
	private final Path qrCodeFilePath;
	private final FileSystemResource qrCodeFileSystemResource;
	private final UploadFileService uploadFileService;

	public QRCodeService(QRCodeWriter qrCodeWriter, QRCodeProperties qrCodeProperties,
		UploadFileService uploadFileService) {
		this.qrCodeWriter = qrCodeWriter;
		this.qrCodeProperties = qrCodeProperties;
		this.qrCodeFilePath = FileSystems.getDefault()
			.getPath(qrCodeProperties.getDirectoryPath() + QR_CODE_GAP_CARE_PNG);
		this.qrCodeFileSystemResource = new FileSystemResource(qrCodeFilePath);
		this.uploadFileService = uploadFileService;
	}

	public FileUploadResponse createQRCodeImageThenUploadToQontak(String contents) {
		try {
			createQRCodeImage(contents);

			return uploadFileService.uploadFileToQontak(qrCodeFileSystemResource);
		} catch (WriterException | IOException e) {
			log.warn(Arrays.toString(e.getStackTrace()));
		}
		return null;
	}

	public void createQRCodeImage(String contents) throws WriterException, IOException {
		BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, qrCodeProperties.getWidth(),
			qrCodeProperties.getHeight());
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCodeFilePath);
	}
}
