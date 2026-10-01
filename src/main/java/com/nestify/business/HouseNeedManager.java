package com.nestify.business;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nestify.dataAccess.HouseNeedRepository;
import com.nestify.dataTransferObject.request.SaveHouseNeedRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseNeedRequestDto;
import com.nestify.dataTransferObject.response.GetHouseNeedByIdResponseDto;
import com.nestify.entities.House;
import com.nestify.entities.HouseNeed;
import com.nestify.entities.User;
import com.nestify.helpers.HouseNeedServiceHelper;
import com.nestify.helpers.HouseServiceHelper;
import com.nestify.helpers.UserServiceHelper;
import com.nestify.mapper.HouseNeedMapper;
import com.nestify.policies.HouseNeedPolicy;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HouseNeedManager implements HouseNeedService {
	private final HouseNeedRepository houseNeedRepository;
	private final UserServiceHelper userServiceHelper;
	private final HouseServiceHelper houseServiceHelper;
	private final HouseNeedServiceHelper houseNeedServiceHelper;
	private final HouseNeedMapper houseNeedMapper;
	private final HouseNeedPolicy houseNeedPolicy;
	@Override
	public List<GetHouseNeedByIdResponseDto> getHouseNeedsFromHouse(Long houseId, Long userId) {
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		houseNeedPolicy.validateHouseNeedOperation(house, userId);
		
		List<HouseNeed> houseNeeds = houseNeedRepository.findByHouseId(houseId);
				
		return houseNeeds.stream()
	          			 .map(houseNeedMapper::toGetHouseNeedByIdResponseDto)
	          			 .toList();
	}

	@Override
	public GetHouseNeedByIdResponseDto addHouseNeed(Long houseId, SaveHouseNeedRequestDto houseNeedRequest) {
		User user = userServiceHelper.getUserOrThrow(houseNeedRequest.getCreatedById());
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		
		houseNeedPolicy.validateHouseNeedOperation(house, user.getId());
		
		HouseNeed houseNeed = new HouseNeed();
		houseNeed.setTitle(houseNeedRequest.getTitle());
		houseNeed.setDescription(houseNeedRequest.getDescription());
		houseNeed.setHouse(house);
		houseNeed.setCreatedBy(user);
		houseNeed.setStatus(houseNeedRequest.getStatus());
		
		HouseNeed savedHouseNeed = houseNeedRepository.save(houseNeed);
		
		return houseNeedMapper.toGetHouseNeedByIdResponseDto(savedHouseNeed);
	}

	@Override
	public GetHouseNeedByIdResponseDto updateHouseNeed(Long houseNeedId, Long houseId, Long actingUserId, UpdateHouseNeedRequestDto houseNeedRequest) {
		User user = userServiceHelper.getUserOrThrow(actingUserId);
		House house = houseServiceHelper.getHouseOrThrow(houseId);

		houseNeedPolicy.validateHouseNeedOperation(house, user.getId());
		
		HouseNeed houseNeed = houseNeedServiceHelper.getHouseNeedOrThrow(houseNeedId);
		houseNeedPolicy.validateHouseNeedBelogsToHouse(houseNeed, houseId);

		houseNeed.setTitle(houseNeedRequest.getTitle());
		houseNeed.setDescription(houseNeedRequest.getDescription());
		houseNeed.setStatus(houseNeedRequest.getStatus());
		
		HouseNeed savedHouseNeed = houseNeedRepository.save(houseNeed);
		
		return houseNeedMapper.toGetHouseNeedByIdResponseDto(savedHouseNeed);
	}

	@Override
	public void deleteHouseNeed(Long houseNeedId, Long houseId, Long actingUserId) {
		HouseNeed houseNeed = houseNeedServiceHelper.getHouseNeedOrThrow(houseNeedId);
		houseNeedPolicy.validateHouseNeedBelogsToHouse(houseNeed, houseId);

		House house = houseServiceHelper.getHouseOrThrow(houseId);
		houseNeedPolicy.validateHouseNeedOperation(house, actingUserId);
		
		houseNeedRepository.delete(houseNeed);
	}
}
