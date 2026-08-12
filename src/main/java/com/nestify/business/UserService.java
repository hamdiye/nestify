package com.nestify.business;

import java.util.List;

import org.springframework.data.domain.Page;

import com.nestify.dataTransferObject.request.UserSaveRequestDto;
import com.nestify.dataTransferObject.request.UserUpdateRequestDto;
import com.nestify.dataTransferObject.response.GetAllUserResponseDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;

public interface UserService {
	
	public Page<GetAllUserResponseDto> getUsers(Integer page, String sortDirection, Integer size, String sortBy);
	public GetUserByIdResponseDto getUserById(Long id);
	public GetUserByIdResponseDto saveUser(UserSaveRequestDto user);
	public GetUserByIdResponseDto updateUser(Long id, UserUpdateRequestDto userUpdateDto);
	public void deleteUser(Long id);
	public List<GetHouseByIdResponseDto> getHousesOfUser(Long userId);

}
