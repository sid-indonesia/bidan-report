package org.sidindonesia.bidanreport.integration.qontak.whatsapp.request;

import org.springframework.core.io.FileSystemResource;

import lombok.Data;

@Data
public class ContactListRequest {
	private String name;
	private String source_type;
	private FileSystemResource file;
}
