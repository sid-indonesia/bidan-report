package org.sidindonesia.bidanreport.integration.qontak.whatsapp.service.util;

import java.nio.file.FileSystems;

import org.sidindonesia.bidanreport.integration.qontak.whatsapp.request.ContactListRequest;
import org.springframework.core.io.FileSystemResource;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContactListUtil {

	public static ContactListRequest createContactListRequest(String campaignName, String fileName) {
		ContactListRequest requestBody = new ContactListRequest();
		requestBody.setName(campaignName);
		requestBody.setFile(new FileSystemResource(FileSystems.getDefault().getPath(fileName)));
		return requestBody;
	}
}
