package com.nestify.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.AuthService;
import com.nestify.dataTransferObject.request.LoginRequestDto;
import com.nestify.dataTransferObject.response.AuthResponseDto;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
	
	private AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginDto) {
		AuthResponseDto response = authService.login(loginDto);
		return ResponseEntity.ok(response);

	}
}
