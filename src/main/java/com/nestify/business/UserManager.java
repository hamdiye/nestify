package com.nestify.business;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.dataAccess.UserRepository;
import com.nestify.dataTransferObject.request.SaveUserRequestDto;
import com.nestify.dataTransferObject.request.UpdateUserRequestDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;
import com.nestify.entities.House;
import com.nestify.entities.User;
import com.nestify.helpers.UserServiceHelper;
import com.nestify.mapper.HouseMapper;
import com.nestify.mapper.UserMapper;
import com.nestify.policies.UserPolicy;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserManager implements UserService {

	private final UserRepository userRepository;
	private final UserServiceHelper userServiceHelper;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final HouseMapper houseMapper;
	private final UserPolicy userPolicy;

	@Override
	public Page<GetUserByIdResponseDto> getUsers(Integer page, String sortDirection, Integer size, String sortBy) {
		
		Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

		Pageable pageable = PageRequest.of(page, size, sort);

		Page<GetUserByIdResponseDto> responsePage = userRepository.findAll(pageable).map(userMapper::toGetUserByIdResponseDto);

		return responsePage;
	}

	@Override
	public GetUserByIdResponseDto getUserById(Long id) {
		User user = userServiceHelper.getUserOrThrow(id);
		return userMapper.toGetUserByIdResponseDto(user);
	}

	@Override
	public GetUserByIdResponseDto saveUser(SaveUserRequestDto userDto) {
		userPolicy.validateUserRegister(userDto);

		String encodedPassword = passwordEncoder.encode(userDto.getPassword());
		User user = new User();
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(encodedPassword);
		User savedUser = userRepository.save(user);
		return userMapper.toGetUserByIdResponseDto(savedUser);
	}

	@Override
	public GetUserByIdResponseDto updateUser(Long id, UpdateUserRequestDto userUpdateData) {
		User user = userServiceHelper.getUserOrThrow(id);
		user.setName(userUpdateData.getName());
		user.setEmail(userUpdateData.getEmail());

		User savedUser = userRepository.save(user);

		return userMapper.toGetUserByIdResponseDto(savedUser);
	}

	@Override
	public void deleteUser(Long id) {
		User user = userServiceHelper.getUserOrThrow(id);
		userRepository.delete(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GetHouseByIdResponseDto> getHousesOfUser(Long userId) {
		User user = userServiceHelper.getUserOrThrow(userId);
		List<GetHouseByIdResponseDto> houses = user.getHouseMemberships().stream().map(houseMember -> {
			House house = houseMember.getHouse();
			return houseMapper.toGetHouseByIdResponseDto(house);

		}).toList();

		return houses;
	}

}
