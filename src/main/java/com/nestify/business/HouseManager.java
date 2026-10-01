package com.nestify.business;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.dataAccess.HouseRepository;
import com.nestify.dataTransferObject.request.AddUserToHouseRequestDto;
import com.nestify.dataTransferObject.request.ChangeMemberRoleRequestDto;
import com.nestify.dataTransferObject.request.JoinHouseByInviteCodeRequestDto;
import com.nestify.dataTransferObject.request.SaveHouseRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseRequestDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.HouseMemberResponseDto;
import com.nestify.entities.EventCategory;
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
public class HouseManager implements HouseService {

	private final HouseRepository houseRepository;
	private final UserServiceHelper userHelper;
	private final HouseServiceHelper houseHelper;
	private final HouseMapper houseMapper;
	private final UserMapper userMapper;
	private final HousePolicy housePolicy;

	@Override
	@Transactional
	public GetHouseByIdResponseDto addHouse(Long actingUserId, 
			SaveHouseRequestDto houseDto) {
		User user = userHelper.getUserOrThrow(actingUserId);

		House house = new House();
		house.setAddress(houseDto.getAddress());
		house.setCity(houseDto.getCity());
		house.setTitle(houseDto.getTitle());
		house.setInviteCode("HOUSE-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());

		house.addMember(user, MemberRole.ADMIN);

		EventCategory defaultCategory = new EventCategory();
		defaultCategory.setTitle("Genel");
		defaultCategory.setDescription("Genel etkinlikler");
		defaultCategory.setColorCode("#7C3AED");
		house.addEventCategory(defaultCategory);

		House savedHouse = houseRepository.save(house);

		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	@Override
	public List<GetHouseByIdResponseDto> getHouses() {
		List<House> houses = houseRepository.findAll();
		return houses.stream()
				.map(houseMapper::toGetHouseByIdResponseDto).toList();
	}

	@Override
	public GetHouseByIdResponseDto getHouseById(Long id) {
		House house = houseHelper.getHouseOrThrow(id);
		return houseMapper.toGetHouseByIdResponseDto(house);
	}

	@Override
	public GetHouseByIdResponseDto updateHouse(Long id, 
			Long actingUserId, 
			UpdateHouseRequestDto houseDto) {
		User user = userHelper.getUserOrThrow(actingUserId);
		House house = houseHelper.getHouseOrThrow(id);
		HouseMember houseMember = houseHelper.getHouseMember(house, user.getId());
		housePolicy.validateAdminAuthority(houseMember);
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
	public List<HouseMemberResponseDto> getUsersOfHouse(Long houseId) {
		House house = houseHelper.getHouseWithMembersOrThrow(houseId);
		List<HouseMemberResponseDto> members = house.getMembers()
													.stream()
													.map(member -> userMapper.toUserSummaryForHouseDto(member.getUser(), member))
													.toList();
		return members;
	}

	@Override
	@Transactional
	public GetHouseByIdResponseDto addMemberToHouse(Long houseId, 
			AddUserToHouseRequestDto addUserToHouseDto,
			Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		User user = userHelper.getUserOrThrow(addUserToHouseDto.getUserId());
		HouseMember actingMember = houseHelper.getHouseMember(house, actingUserId);
		housePolicy.validateMemberAddition(house, user.getId(), actingMember);
		house.addMember(user, addUserToHouseDto.getMemberRole());
		House savedHouse = houseRepository.save(house);
		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	@Override
	@Transactional
	public GetHouseByIdResponseDto addMemberToHouseByInviteCode(JoinHouseByInviteCodeRequestDto joinHouseRequestDto,
			Long actingUserId) {
		House house = houseHelper.getHouseOrThrowByInviteCode(joinHouseRequestDto.getInviteCode());
		User user = userHelper.getUserOrThrow(actingUserId);
		housePolicy.validateMemberIncludeHouse(house, user.getId());
		house.addMember(user, MemberRole.MEMBER);
		House savedHouse = houseRepository.save(house);
		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}
	
	@Override
	@Transactional
	public GetHouseByIdResponseDto removeMemberFromHouse(Long houseId, 
			Long userId,
			Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		User targetUser = userHelper.getUserOrThrow(userId);
		HouseMember actingMember = houseHelper.getHouseMember(house, actingUserId);
		housePolicy.validateMemberRemoval(house, targetUser.getId(), actingMember);
		house.removeMember(targetUser);
		House savedHouse = houseRepository.save(house);

		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	@Override
	@Transactional
	public HouseMemberResponseDto changeMemberRole(Long houseId, 
			Long userId,
			ChangeMemberRoleRequestDto changeMemberRoleDto, Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		User targetUser = userHelper.getUserOrThrow(userId);
		HouseMember actingMember = houseHelper.getHouseMember(house, actingUserId);
		housePolicy.validateChangeMemberRole(house, targetUser.getId(), actingMember);
		HouseMember targetMember = houseHelper.getHouseMember(house, targetUser.getId());
		targetMember.setMemberRole(changeMemberRoleDto.getMemberRole());

		return userMapper.toUserSummaryForHouseDto(targetUser, targetMember);
	}
}
