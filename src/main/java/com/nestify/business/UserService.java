package com.nestify.business;

import java.util.List;

import org.springframework.data.domain.Page;

import com.nestify.dataTransferObject.request.SaveUserRequestDto;
import com.nestify.dataTransferObject.request.UpdateUserRequestDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;

public interface UserService {
	
	Page<GetUserByIdResponseDto> getUsers(Integer page, String sortDirection, Integer size, String sortBy);
	GetUserByIdResponseDto getUserById(Long id);
	GetUserByIdResponseDto saveUser(SaveUserRequestDto user);
	GetUserByIdResponseDto updateUser(Long id, UpdateUserRequestDto userUpdateDto);
	void deleteUser(Long id);
	List<GetHouseByIdResponseDto> getHousesOfUser(Long userId);

}
