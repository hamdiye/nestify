package com.nestify.policies;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.request.SaveUserRequestDto;
import com.nestify.dataTransferObject.request.UpdateUserRequestDto;
import com.nestify.entities.User;
import com.nestify.helpers.UserServiceHelper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserPolicy {

	private final UserServiceHelper userServiceHelper;

	public void validateUserRegister(SaveUserRequestDto userRequestDto) {
		userServiceHelper.emailIsAlreadyInUse(userRequestDto.getEmail());
	}

	public void validateUserUpdate(User user, UpdateUserRequestDto userUpdateDto) {
		if (!user.getEmail().equals(userUpdateDto.getEmail())) {
			userServiceHelper.emailIsAlreadyInUse(userUpdateDto.getEmail());
		}
	}
	
}
