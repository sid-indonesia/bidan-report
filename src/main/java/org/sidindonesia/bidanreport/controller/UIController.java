package org.sidindonesia.bidanreport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

	@GetMapping("/ExcelSheet/$validate")
	public String goToValidatePage() {
		return "validate";
	}
}
