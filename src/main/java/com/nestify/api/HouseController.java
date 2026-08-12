package com.nestify.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.HouseService;
import com.nestify.dataTransferObject.request.AddUserToHouseRequestDto;
import com.nestify.dataTransferObject.request.ChangeMemberRoleRequestDto;
import com.nestify.dataTransferObject.request.HouseSaveRequestDto;
import com.nestify.dataTransferObject.request.HouseUpdateRequestDto;
import com.nestify.dataTransferObject.request.RemoveUserToHouseRequestDto;
import com.nestify.dataTransferObject.response.GetAllHouseResponseDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.UserSummaryForHouseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/houses")
public class HouseController {
	private HouseService houseService;
	
	
	public HouseController(HouseService houseService) {
		this.houseService = houseService;
	}
	
	@PostMapping("/add")
	public GetHouseByIdResponseDto addHouse(@Valid @RequestBody HouseSaveRequestDto houseDto) {
		return houseService.addHouse(houseDto);
	}
	
	@GetMapping
	public List<GetAllHouseResponseDto> getHouses(){
		return houseService.getHouses();
	}
	
	@GetMapping("/{id}")
	public GetHouseByIdResponseDto getHouseById(Long id) {
		return houseService.getHouseById(id);
	}
	
	@PutMapping("/update/{id}")
	public GetHouseByIdResponseDto updateHouse(@PathVariable Long id, @Valid @RequestBody HouseUpdateRequestDto houseDto) {
		return houseService.updateHouse(id, houseDto);
	}
	
	@PutMapping("/delete/{id}")
	public void deleteHouse(@PathVariable Long id) {
		houseService.deleteHouse(id);
	}
	
	@GetMapping("/{houseId}/members")
	public List<UserSummaryForHouseDto> getUsersOfHouse(@PathVariable Long houseId){
		return houseService.getUsersOfHouse(houseId);
	}
	
	@PostMapping("/{houseId}/members/addMember")
	public GetHouseByIdResponseDto addMemberToHouse(@PathVariable Long houseId, @Valid @RequestBody AddUserToHouseRequestDto addUserToHouseDto, @RequestHeader("X-Acting-User-Id") Long actingUserId){
		return houseService.addMemberToHouse(houseId, addUserToHouseDto, actingUserId);
	}
	
	@PostMapping("/{houseId}/members/removeMember")
	public GetHouseByIdResponseDto removeMemberToHouse(@PathVariable Long houseId, @Valid @RequestBody RemoveUserToHouseRequestDto removeUserToHouseDto, @RequestHeader("X-Acting-User-Id") Long actingUserId){
		return houseService.removeMemberToHouse(houseId, removeUserToHouseDto, actingUserId);
	}
	
	@PostMapping("/{houseId}/members/{userId}/changeRole")
	public UserSummaryForHouseDto changeMemberRole(@PathVariable Long houseId, @PathVariable Long userId, @Valid @RequestBody ChangeMemberRoleRequestDto changeMemberRoleDto, @RequestHeader("X-Acting-User-Id") Long actingUserId) {
		return houseService.changeMemberRole(houseId, userId, changeMemberRoleDto, actingUserId);
	}
	
	
	
}
