package com.nestify.business;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nestify.dataTransferObject.request.LoginRequestDto;
import com.nestify.dataTransferObject.response.AuthResponseDto;
import com.nestify.entities.User;
import com.nestify.helpers.UserServiceHelper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthManager implements AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserServiceHelper userServiceHelper;

	@Override
	public AuthResponseDto login(LoginRequestDto loginDto) {
		User user = userServiceHelper.getUserByEmailOrThrow(loginDto.getEmail());
		boolean isPasswordMatch = passwordEncoder.matches(loginDto.getPassword(), user.getPassword());
		if (!isPasswordMatch) {
			throw new RuntimeException("E-posta veya şifre hatalı.");
		}

		return AuthResponseDto.builder()
				.userId(user.getId())
				.email(user.getEmail())
				.fullName(user.getName())
				.build();
	}
}
