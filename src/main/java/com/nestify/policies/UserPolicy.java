package com.nestify.policies;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.request.SaveUserRequestDto;
import com.nestify.helpers.UserServiceHelper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserPolicy {
	
	private UserServiceHelper userServiceHelper;
	
	public void validateUserRegister(SaveUserRequestDto userRequestDto) {
		if(userServiceHelper.emailIsExist(userRequestDto.getEmail())) {
			throw new RuntimeException("Bu email adresi sistemde zaten kayıtlı!");
		}
	}
	
	
}
