package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.AddUserToHouseRequestDto;
import com.nestify.dataTransferObject.request.ChangeMemberRoleRequestDto;
import com.nestify.dataTransferObject.request.JoinHouseByInviteCodeRequestDto;
import com.nestify.dataTransferObject.request.SaveHouseRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseRequestDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.HouseMemberResponseDto;

public interface HouseService {
	List<GetHouseByIdResponseDto> getHouses();
	GetHouseByIdResponseDto getHouseById(Long id);
	GetHouseByIdResponseDto addHouse(Long actingUserId, SaveHouseRequestDto houseDto);
	GetHouseByIdResponseDto updateHouse(Long id, Long actingUserId, UpdateHouseRequestDto houseDto);
	List<HouseMemberResponseDto> getUsersOfHouse(Long houseId);
	GetHouseByIdResponseDto addMemberToHouse(Long houseId, AddUserToHouseRequestDto addUserToHouseDto, Long actingUserId);
	GetHouseByIdResponseDto addMemberToHouseByInviteCode(JoinHouseByInviteCodeRequestDto addUserToHouseDto, Long actingUserId);
	GetHouseByIdResponseDto removeMemberFromHouse(Long houseId, Long userId, Long actingUserId);
	HouseMemberResponseDto changeMemberRole(Long houseId, Long userId, ChangeMemberRoleRequestDto changeMemberRoleDto, Long actingUserId);
	void deleteHouse(Long id);
}
