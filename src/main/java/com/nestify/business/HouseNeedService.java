package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.SaveHouseNeedRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseNeedRequestDto;
import com.nestify.dataTransferObject.response.GetHouseNeedByIdResponseDto;

public interface HouseNeedService {
	List<GetHouseNeedByIdResponseDto> getHouseNeedsFromHouse(Long houseId, Long userId);
	GetHouseNeedByIdResponseDto addHouseNeed(Long houseId, SaveHouseNeedRequestDto houseNeedRequest);
	GetHouseNeedByIdResponseDto updateHouseNeed(Long houseNeedId, Long houseId, Long actingUserId, UpdateHouseNeedRequestDto houseNeedRequest);
	void deleteHouseNeed(Long houseNeedId, Long houseId, Long actingUserId);

}
