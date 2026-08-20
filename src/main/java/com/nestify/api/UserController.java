package com.nestify.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.UserService;
import com.nestify.dataTransferObject.request.SaveUserRequestDto;
import com.nestify.dataTransferObject.request.UpdateUserRequestDto;
import com.nestify.dataTransferObject.response.GetAllUserResponseDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

	private UserService userService;
	

	@GetMapping
	public ResponseEntity<Page<GetAllUserResponseDto>> getUsers(@PathVariable @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "asc") String sortDirection, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "id") String sortBy ){
		Page<GetAllUserResponseDto> users = userService.getUsers(page, sortDirection, size, sortBy);
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/{id}")
	public GetUserByIdResponseDto getUser(@PathVariable Long id){
		return userService.getUserById(id);
	}
	
	@GetMapping("/{id}/houses")
	public List<GetHouseByIdResponseDto> getHousesOfUser(@PathVariable Long id){
		return userService.getHousesOfUser(id);
	}
	
	@PostMapping("/add")
	public GetUserByIdResponseDto addUser(@Valid @RequestBody SaveUserRequestDto userSaveRequestDto) {
		System.out.println(userSaveRequestDto.getName());
		return userService.saveUser(userSaveRequestDto);
	}
	
	@PutMapping("/update/{id}")
	public GetUserByIdResponseDto updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDto userUpdateRequestDto) {
		return userService.updateUser(id, userUpdateRequestDto);
	}
	
	@DeleteMapping("/delete/{id}")
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}
}
