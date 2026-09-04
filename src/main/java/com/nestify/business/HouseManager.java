package com.nestify.business;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.dataAccess.HouseRepository;
import com.nestify.dataTransferObject.request.AddUserToHouseRequestDto;
import com.nestify.dataTransferObject.request.ChangeMemberRoleRequestDto;
import com.nestify.dataTransferObject.request.JoinHouseByInviteCodeRequestDto;
import com.nestify.dataTransferObject.request.RemoveUserToHouseRequestDto;
import com.nestify.dataTransferObject.request.SaveHouseRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseRequestDto;
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
public class HouseManager implements HouseService {

	private final HouseRepository houseRepository;
	private final UserServiceHelper userHelper;
	private final HouseServiceHelper houseHelper;
	private final HouseMapper houseMapper;
	private final UserMapper userMapper;
	private final HousePolicy housePolicy;

	/**
	 * Creates a new house and assigns the creator user as ADMIN.
	 *
	 * @param houseDto DTO containing house attributes and creator user ID
	 * @return Created house details DTO
	 */
	@Override
	@Transactional
	public GetHouseByIdResponseDto addHouse(SaveHouseRequestDto houseDto) {
		User user = userHelper.getUserOrThrow(houseDto.getUserId());

		House house = new House();
		house.setAddress(houseDto.getAddress());
		house.setCity(houseDto.getCity());
		house.setTitle(houseDto.getTitle());
		house.setInviteCode("HOUSE-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());

		house.addMember(user, MemberRole.ADMIN);

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

		if (!houseMember.isAdmin()) {
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

	/**
	 * Adds a user to an existing house by an acting admin member.
	 *
	 * @param houseId The ID of the target house
	 * @param addUserToHouseDto DTO containing user ID and assigned role
	 * @param actingUserId ID of the user performing the operation
	 * @return Updated house details DTO
	 */
	@Override
	@Transactional
	public GetHouseByIdResponseDto addMemberToHouse(Long houseId, AddUserToHouseRequestDto addUserToHouseDto,
			Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		User user = userHelper.getUserOrThrow(addUserToHouseDto.getUserId());
		HouseMember actingMember = houseHelper.getHouseMember(house, actingUserId);

		housePolicy.validateMemberAddition(house, user.getId(), actingMember);

		house.addMember(user, addUserToHouseDto.getRole());
		House savedHouse = houseRepository.save(house);
		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	/**
	 * Adds an acting user to a house using a valid invite code.
	 *
	 * @param joinHouseRequestDto DTO containing the invite code
	 * @param actingUserId ID of the user joining the house
	 * @return Updated house details DTO
	 */
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
	
	/**
	 * Removes a member from a house or allows a member to leave the house.
	 *
	 * @param houseId The ID of the house
	 * @param removeUserToHouseDto DTO containing the target user ID to remove
	 * @param actingUserId ID of the user performing the removal or self-removal
	 * @return Updated house details DTO
	 */
	@Override
	@Transactional
	public GetHouseByIdResponseDto removeMemberToHouse(Long houseId, RemoveUserToHouseRequestDto removeUserToHouseDto,
			Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		User targetUser = userHelper.getUserOrThrow(removeUserToHouseDto.getUserId());
		HouseMember actingMember = houseHelper.getHouseMember(house, actingUserId);

		housePolicy.validateMemberRemoval(house, targetUser.getId(), actingMember);

		house.removeMember(targetUser);
		House savedHouse = houseRepository.save(house);

		return houseMapper.toGetHouseByIdResponseDto(savedHouse);
	}

	/**
	 * Changes the membership role of a user in a house.
	 *
	 * @param houseId The ID of the house
	 * @param userId The ID of the target user
	 * @param changeMemberRoleDto DTO containing the new role
	 * @param actingUserId ID of the acting admin user
	 * @return UserSummaryForHouseDto of the updated member
	 */
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
				targetMember.getMemberRole());
	}



}
