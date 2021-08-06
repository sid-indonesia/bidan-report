package org.sidindonesia.bidanreport.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class LogoutController {

	/**
	 * {@code GET  /api/logout} : logout the current user.
	 *
	 * @param request the {@link HttpServletRequest}.
	 * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
	 * @throws ServletException
	 */
	@GetMapping("/api/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) throws ServletException {
		log.debug("Logging out User: {}", request.getUserPrincipal().getName());
		request.logout();
		request.getSession().invalidate();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
