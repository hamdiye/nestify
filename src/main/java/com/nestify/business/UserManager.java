package com.nestify.business;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nestify.dataAccess.UserRepository;
import com.nestify.dataTransferObject.request.UserSaveRequestDto;
import com.nestify.dataTransferObject.request.UserUpdateRequestDto;
import com.nestify.dataTransferObject.response.GetAllUserResponseDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;
import com.nestify.entities.User;

@Service
public class UserManager implements UserService {
	
	private UserRepository userRepository;
	
	
	public UserManager(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Page<GetAllUserResponseDto> getUsers(Integer page, String sortDirection, Integer size, String sortBy) {
		
		Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
							Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		
		Pageable paginationFilter = PageRequest.of(page, size, sort);
		
		Page<User> userPage = userRepository.findAll(paginationFilter);
		
		Page<GetAllUserResponseDto> responsePage = userPage.map(user -> new GetAllUserResponseDto(
		        user.getId(),
		        user.getName(),
		        user.getEmail()
		));
		
		return responsePage;
	}

	@Override
	public GetUserByIdResponseDto getUserById(Long id) {
		User user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
		
		return new GetUserByIdResponseDto(
							user.getId(),
		                    user.getName(),
		                    user.getEmail()
				);
	}

	@Override
	public GetUserByIdResponseDto saveUser(UserSaveRequestDto userDto) {
		User user = new User();
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		User savedUser = userRepository.save(user);
		return new GetUserByIdResponseDto(
				savedUser.getId(),
				savedUser.getName(),
				savedUser.getEmail()
				);
	}

	@Override
	public GetUserByIdResponseDto updateUser(Long id, UserUpdateRequestDto userUpdateData) {
		User user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
		user.setName(userUpdateData.getName());
		user.setEmail(userUpdateData.getEmail());
		user.setPassword(userUpdateData.getPassword());
		
		User savedUser = userRepository.save(user);
		
		return new GetUserByIdResponseDto(
				savedUser.getId(),
				savedUser.getName(),
				savedUser.getEmail()
				);
	}

	@Override
	public void deleteUser(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));
		userRepository.delete(user);
	}

}
