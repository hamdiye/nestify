package com.nestify.business;

import com.nestify.dataTransferObject.request.LoginRequestDto;
import com.nestify.dataTransferObject.response.AuthResponseDto;

public interface AuthService {
	public AuthResponseDto login(LoginRequestDto loginDto);
}
