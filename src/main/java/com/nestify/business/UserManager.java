package com.nestify.business;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.dataAccess.UserRepository;
import com.nestify.dataTransferObject.request.SaveUserRequestDto;
import com.nestify.dataTransferObject.request.UpdateUserRequestDto;
import com.nestify.dataTransferObject.response.GetAllUserResponseDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;
import com.nestify.entities.House;
import com.nestify.entities.User;
import com.nestify.mapper.HouseMapper;
import com.nestify.mapper.UserMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserManager implements UserService {
	
	private UserRepository userRepository;
	private UserMapper userMapper;
	private HouseMapper houseMapper;

	@Override
	public Page<GetAllUserResponseDto> getUsers(Integer page, String sortDirection, Integer size, String sortBy) {
		
		Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
							Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		
		Pageable paginationFilter = PageRequest.of(page, size, sort);
		
		Page<User> userPage = userRepository.findAll(paginationFilter);
		
		Page<GetAllUserResponseDto> responsePage = userPage.map(user -> userMapper.toGetAllUserResponseDto(user));
		
		return responsePage;
	}

	@Override
	public GetUserByIdResponseDto getUserById(Long id) {
		User user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
		
		return userMapper.toGetUserByIdResponseDto(user);
	}

	@Override
	public GetUserByIdResponseDto saveUser(SaveUserRequestDto userDto) {
		User user = new User();
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		User savedUser = userRepository.save(user);
		return userMapper.toGetUserByIdResponseDto(savedUser);
	}

	@Override
	public GetUserByIdResponseDto updateUser(Long id, UpdateUserRequestDto userUpdateData) {
		User user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
		user.setName(userUpdateData.getName());
		user.setEmail(userUpdateData.getEmail());
		user.setPassword(userUpdateData.getPassword());
		
		User savedUser = userRepository.save(user);
		
		return userMapper.toGetUserByIdResponseDto(savedUser);
	}

	@Override
	public void deleteUser(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
		userRepository.delete(user);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<GetHouseByIdResponseDto> getHousesOfUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
		List<GetHouseByIdResponseDto> houses = user.getHouseMemberships()
													 .stream()
													 .map(houseMember -> {
														 House house = houseMember.getHouse();
														 return houseMapper.toGetHouseByIdResponseDto(house);
							
													  }).toList();

		return houses;
	}

}
