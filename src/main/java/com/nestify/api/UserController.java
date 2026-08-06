package com.nestify.api;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.nestify.business.UserService;
import com.nestify.dataTransferObject.request.UserSaveRequestDto;
import com.nestify.dataTransferObject.request.UserUpdateRequestDto;
import com.nestify.dataTransferObject.response.GetAllUserResponseDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private UserService userService;
	
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<Page<GetAllUserResponseDto>> getUsers(@PathVariable @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "asc") String sortDirection, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "id") String sortBy ){
		Page<GetAllUserResponseDto> users = userService.getUsers(page, sortDirection, size, sortBy);
		return ResponseEntity.ok(users);
	}
	
	@PostMapping("/{id}")
	public GetUserByIdResponseDto getUser(@RequestBody Long id){
		return userService.getUserById(id);
	}
	
	@PostMapping("/add")
	public GetUserByIdResponseDto addUser(@Valid @RequestBody UserSaveRequestDto userSaveRequestDto) {
		System.out.println(userSaveRequestDto.getName());
		return userService.saveUser(userSaveRequestDto);
	}
	
	@PutMapping("/update/{id}")
	public GetUserByIdResponseDto updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
		return userService.updateUser(id, userUpdateRequestDto);
	}
	
	@PutMapping("/delete/{id}")
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}
}
