package com.nestify.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nestify.dataAccess.UserRepository;
import com.nestify.dataTransferObject.request.SaveUserRequestDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;
import com.nestify.entities.User;
import com.nestify.mapper.HouseMapper;
import com.nestify.mapper.UserMapper;
import com.nestify.policies.UserPolicy;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private UserMapper userMapper;
	@Mock
	private HouseMapper houseMapper;
	@Mock
	private UserPolicy userPolicy;
	@InjectMocks
	private UserManager userService;

	@Test
	public void getUserById_whenUserExists_shouldReturnDto() {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@gmail.com");

		GetUserByIdResponseDto userResponseDto = new GetUserByIdResponseDto();
		userResponseDto.setId(1L);
		userResponseDto.setEmail("test@gmail.com");

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(userMapper.toGetUserByIdResponseDto(user)).thenReturn(userResponseDto);

		GetUserByIdResponseDto actualResult = userService.getUserById(1L);
		assertNotNull(actualResult);
		assertEquals(userResponseDto.getId(), actualResult.getId());
		assertEquals(userResponseDto.getEmail(), actualResult.getEmail());
	}

	@Test
	public void getUserById_whenUserDoesNotExist_shouldThrowRuntimeException() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			userService.getUserById(1L);
		});

		assertEquals("Kullanıcı bulunamadı: 1", exception.getMessage());
		verifyNoInteractions(userMapper);
	}
	
	@Test
	public void saveUser_whenValidRequest_shouldReturnDto() {
		SaveUserRequestDto userRequest = new SaveUserRequestDto();
		userRequest.setName("Test Name");
		userRequest.setEmail("test@gmail.com");
		userRequest.setPassword("testpassword");
		
		User user = new User();
		user.setId(1L);
		user.setName("Test Name");
		user.setEmail("test@gmail.com");
		user.setPassword("testpassword");
		
		GetUserByIdResponseDto userResponseDto = new GetUserByIdResponseDto();
		userResponseDto.setId(1L);
		userResponseDto.setEmail("test@gmail.com");
		
		when(passwordEncoder.encode("testpassword")).thenReturn("encodedPassword123");
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(userMapper.toGetUserByIdResponseDto(user)).thenReturn(userResponseDto);
		
		GetUserByIdResponseDto response = userService.saveUser(userRequest);
		
		assertNotNull(response);
		assertEquals(response.getId(), userResponseDto.getId());
		assertEquals(response.getEmail(), userResponseDto.getEmail());
		
		verify(passwordEncoder).encode("testpassword");
		verify(userPolicy).validateUserRegister(userRequest);
		verify(userRepository).save(any(User.class));
		verify(userMapper).toGetUserByIdResponseDto(user);
		
	}
}
