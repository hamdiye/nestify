package com.nestify.api;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.HouseService;
import com.nestify.core.UserPrincipal;
import com.nestify.dataTransferObject.request.AddUserToHouseRequestDto;
import com.nestify.dataTransferObject.request.ChangeMemberRoleRequestDto;
import com.nestify.dataTransferObject.request.JoinHouseByInviteCodeRequestDto;
import com.nestify.dataTransferObject.request.SaveHouseRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseRequestDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.HouseMemberResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/houses")
@AllArgsConstructor
public class HouseController {
	
	private final HouseService houseService;
	
	@PostMapping
	public GetHouseByIdResponseDto addHouse(@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody SaveHouseRequestDto houseDto) {
		return houseService.addHouse(currentUser.getId(), houseDto);
	}

	@GetMapping
	public List<GetHouseByIdResponseDto> getHouses() {
		return houseService.getHouses();
	}

	@GetMapping("/{id}")
	public GetHouseByIdResponseDto getHouseById(@PathVariable Long id) {
		return houseService.getHouseById(id);
	}

	@PutMapping("/{id}")
	public GetHouseByIdResponseDto updateHouse(@PathVariable Long id,
			@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody UpdateHouseRequestDto houseDto) {
		return houseService.updateHouse(id, currentUser.getId(), houseDto);
	}

	@DeleteMapping("/{id}")
	public void deleteHouse(@PathVariable Long id) {
		houseService.deleteHouse(id);
	}

	@GetMapping("/{houseId}/members")
	public List<HouseMemberResponseDto> getUsersOfHouse(@PathVariable Long houseId) {
		return houseService.getUsersOfHouse(houseId);
	}

	@PostMapping("/{houseId}/members")
	public GetHouseByIdResponseDto addMemberToHouse(@PathVariable Long houseId,
			@Valid @RequestBody AddUserToHouseRequestDto addUserToHouseDto,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		return houseService.addMemberToHouse(houseId, addUserToHouseDto, currentUser.getId());
	}

	@PostMapping("/{houseId}/members/join")
	public GetHouseByIdResponseDto addMemberToHouseByInviteCode(@Valid @RequestBody JoinHouseByInviteCodeRequestDto joinHouseRequestDto,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		return houseService.addMemberToHouseByInviteCode(joinHouseRequestDto, currentUser.getId());
	}
	
	@DeleteMapping("/{houseId}/members/{userId}")
	public GetHouseByIdResponseDto removeMemberFromHouse(@PathVariable Long houseId,
			@PathVariable Long userId,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		return houseService.removeMemberFromHouse(houseId, userId, currentUser.getId());
	}

	@PatchMapping("/{houseId}/members/{userId}/role")
	public HouseMemberResponseDto changeMemberRole(@PathVariable Long houseId, @PathVariable Long userId,
			@Valid @RequestBody ChangeMemberRoleRequestDto changeMemberRoleDto,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		return houseService.changeMemberRole(houseId, userId, changeMemberRoleDto, currentUser.getId());
	}

}
