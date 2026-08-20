package com.nestify.business;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nestify.dataAccess.HouseNeedRepository;
import com.nestify.dataTransferObject.request.DeleteHouseNeedRequestDto;
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
	private HouseNeedRepository houseNeedRepository;
	private UserServiceHelper userServiceHelper;
	private HouseServiceHelper houseServiceHelper;
	private HouseNeedServiceHelper houseNeedServiceHelper;
	private HouseNeedMapper houseNeedMapper;
	private HouseNeedPolicy houseNeedPolicy;
	@Override
	public List<GetHouseNeedByIdResponseDto> getHouseNeedsFromHouse(Long houseId, Long userId) {
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		houseNeedPolicy.validateHouseNeedOperation(house, userId);
		
		List<GetHouseNeedByIdResponseDto> houseNeeds = house.getHouseNeeds()
															.stream()
															.map(houseNeed -> houseNeedMapper.toGetHouseNeedByIdResponseDto(houseNeed))
															.toList();
		return houseNeeds;
	}

	@Override
	public GetHouseNeedByIdResponseDto addHouseNeed(SaveHouseNeedRequestDto houseNeedRequest) {
		User user = userServiceHelper.getUserOrThrow(houseNeedRequest.getCreatedBy_id());
		House house = houseServiceHelper.getHouseOrThrow(houseNeedRequest.getHouse_id());
		
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
	public GetHouseNeedByIdResponseDto updateHouseNeed(UpdateHouseNeedRequestDto houseNeedRequest) {
		User user = userServiceHelper.getUserOrThrow(houseNeedRequest.getCreatedById());
		House house = houseServiceHelper.getHouseOrThrow(houseNeedRequest.getHouseId());
		
		houseNeedPolicy.validateHouseNeedOperation(house, user.getId());
		
		HouseNeed houseNeed = houseNeedServiceHelper.getHouseNeedOrThrow(houseNeedRequest.getId());
		houseNeed.setTitle(houseNeedRequest.getTitle());
		houseNeed.setDescription(houseNeedRequest.getDescription());
		houseNeed.setHouse(house);
		houseNeed.setCreatedBy(user);
		houseNeed.setStatus(houseNeedRequest.getStatus());
		
		HouseNeed savedHouseNeed = houseNeedRepository.save(houseNeed);
		
		return houseNeedMapper.toGetHouseNeedByIdResponseDto(savedHouseNeed);
	}

	@Override
	public void deleteHouseNeed(DeleteHouseNeedRequestDto houseNeedRequest) {
		House house = houseServiceHelper.getHouseOrThrow(houseNeedRequest.getHouseId());
		HouseNeed houseNeed = houseNeedServiceHelper.getHouseNeedOrThrow(houseNeedRequest.getHouseNeedId());
		houseNeedPolicy.validateHouseNeedOperation(house, houseNeedRequest.getUserId());
		
		houseNeedRepository.delete(houseNeed);
	}

	

}
