package org.sidindonesia.bidanreport.integration.qontak.whatsapp.request;

import org.springframework.core.io.FileSystemResource;

import lombok.Data;

@Data
public class ContactListRequest {
	private String name;
	private String source_type = "spreadsheet"; // Possible values are: spreadsheet, crm_static, crm_dynamic
	private FileSystemResource file;
}
