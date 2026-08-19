package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.AddUserToHouseRequestDto;
import com.nestify.dataTransferObject.request.ChangeMemberRoleRequestDto;
import com.nestify.dataTransferObject.request.SaveHouseRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseRequestDto;
import com.nestify.dataTransferObject.request.RemoveUserToHouseRequestDto;
import com.nestify.dataTransferObject.response.GetAllHouseResponseDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.UserSummaryForHouseDto;

public interface HouseService {
	public List<GetAllHouseResponseDto> getHouses();
	public GetHouseByIdResponseDto getHouseById(Long id);
	public GetHouseByIdResponseDto addHouse(SaveHouseRequestDto houseDto);
	public GetHouseByIdResponseDto updateHouse(Long id, UpdateHouseRequestDto houseDto);
	public List<UserSummaryForHouseDto> getUsersOfHouse(Long houseId);
	public GetHouseByIdResponseDto addMemberToHouse(Long houseId, AddUserToHouseRequestDto addUserToHouseDto, Long actingUserId);
	public GetHouseByIdResponseDto removeMemberToHouse(Long houseId, RemoveUserToHouseRequestDto removeUserToHouseDto, Long actingUserId);
	public UserSummaryForHouseDto changeMemberRole(Long houseId, Long userId, ChangeMemberRoleRequestDto changeMemberRoleDto, Long actingUserId);
	public void deleteHouse(Long id);
	

}
