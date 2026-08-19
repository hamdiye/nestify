package com.nestify.business;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.dataAccess.HouseRepository;
import com.nestify.dataTransferObject.request.AddUserToHouseRequestDto;
import com.nestify.dataTransferObject.request.ChangeMemberRoleRequestDto;
import com.nestify.dataTransferObject.request.SaveHouseRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseRequestDto;
import com.nestify.dataTransferObject.request.RemoveUserToHouseRequestDto;
import com.nestify.dataTransferObject.response.GetAllHouseResponseDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.UserSummaryForHouseDto;
import com.nestify.entities.House;
import com.nestify.entities.HouseMember;
import com.nestify.entities.User;
import com.nestify.entities.enums.MemberRole;
import com.nestify.helpers.HouseServiceHelper;
import com.nestify.helpers.UserServiceHelper;
import com.nestify.mapper.HouseMapper;
import com.nestify.mapper.UserMapper;
import com.nestify.policies.HousePolicy;

import lombok.AllArgsConstructor;



@Service
@AllArgsConstructor
public class HouseManager implements HouseService{

	private final HouseRepository houseRepository;
	private final UserServiceHelper userHelper;
	private final HouseServiceHelper houseHelper;
	private final HouseMapper houseMapper;
	private final UserMapper userMapper;
	private final HousePolicy housePolicy;
	
	

	@Override
	public GetHouseByIdResponseDto addHouse(SaveHouseRequestDto houseDto) {
		User user = userHelper.getUserOrThrow(houseDto.getUserId());
		
		House house = new House();
		house.setAddress(houseDto.getAddress());
		house.setCity(houseDto.getCity());
		house.setTitle(houseDto.getTitle());
		house.setInvateCode("HOUSE-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
		
		house.AddMember(user, MemberRole.ADMIN);
		
		House savedHouse = houseRepository.save(house);
		
		
		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	@Override
	public List<GetAllHouseResponseDto> getHouses() {
		List<House> houses = houseRepository.findAll();
		List<GetAllHouseResponseDto> houseDtos = houses.stream()
													   .map(house -> houseMapper.toGetAllHouseResponseDto(house)).toList();
		return houseDtos;
	}

	@Override
	public GetHouseByIdResponseDto getHouseById(Long id) {
		House house = houseHelper.getHouseOrThrow(id);
		return houseMapper.toGetHouseByIdResponseDto(house);
	}

	@Override
	public GetHouseByIdResponseDto updateHouse(Long id, UpdateHouseRequestDto houseDto) {
		User user = userHelper.getUserOrThrow(houseDto.getUserId());
		
		House house = houseHelper.getHouseOrThrow(id);		
			
		HouseMember houseMember = houseHelper.getHouseMember(house, user.getId());
				
		if(!houseMember.isAdmin()) {
			throw new RuntimeException("Ev bilgilerini güncellemek için ADMIN yetkisi gereklidir!");
		}
		
		house.setAddress(houseDto.getAddress());
		house.setCity(houseDto.getCity());
		house.setTitle(houseDto.getTitle());
		
		House savedHouse = houseRepository.save(house);
		
		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	@Override
	public void deleteHouse(Long id) {
		House house = houseHelper.getHouseOrThrow(id);
		housePolicy.validateDestroyHouse(house);
		houseRepository.delete(house);
	}

	@Override
	public List<UserSummaryForHouseDto> getUsersOfHouse(Long houseId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		List<UserSummaryForHouseDto> members = house.getMembers()
														  .stream()
														  .map(member -> userMapper.toUserSummaryForHouseDto(member.getUser(), member))
														  .toList();
		return members;
	}

	@Override
	public GetHouseByIdResponseDto addMemberToHouse(Long houseId, AddUserToHouseRequestDto addUserToHouseDto, Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		User user = userHelper.getUserOrThrow(addUserToHouseDto.getUserId());
		HouseMember actingMember = houseHelper.getHouseMember(house, actingUserId);
		
		housePolicy.validateMemberAddition(house, user.getId(), actingMember);
		
		house.AddMember(user, addUserToHouseDto.getRole());
		House savedHouse = houseRepository.save(house);
		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	@Override
	public GetHouseByIdResponseDto removeMemberToHouse(Long houseId, RemoveUserToHouseRequestDto removeUserToHouseDto, Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		User targetUser = userHelper.getUserOrThrow(removeUserToHouseDto.getUserId());
		HouseMember actingMember = houseHelper.getHouseMember(house, actingUserId);
		
		housePolicy.validateMemberRemoval(house, targetUser.getId(), actingMember);
			

		house.RemoveMember(targetUser);
		House savedHouse = houseRepository.save(house);
		
		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	@Override
	@Transactional
	public UserSummaryForHouseDto changeMemberRole(Long houseId, Long userId,
						ChangeMemberRoleRequestDto changeMemberRoleDto, Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		User targetUser = userHelper.getUserOrThrow(changeMemberRoleDto.getUserId());
		HouseMember actingMember = houseHelper.getHouseMember(house, actingUserId);
		
		housePolicy.validateChangeMemberRole(house, targetUser.getId(), actingMember);
		
		HouseMember targetMember = houseHelper.getHouseMember(house, targetUser.getId());
		targetMember.setMemberRole(changeMemberRoleDto.getRole());
		
		return new UserSummaryForHouseDto(
				targetUser.getId(),
				targetUser.getName(),
				targetUser.getEmail(),
				targetMember.getJoinedAt(),
				targetMember.getMemberRole()
				);
	}
	
	
	
}
