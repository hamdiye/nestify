package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.DeleteHouseNeedRequestDto;
import com.nestify.dataTransferObject.request.SaveHouseNeedRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseNeedRequestDto;
import com.nestify.dataTransferObject.response.GetHouseNeedByIdResponseDto;

public interface HouseNeedService {
	public List<GetHouseNeedByIdResponseDto> getHouseNeedsFromHouse(Long houseId, Long userId);
	public GetHouseNeedByIdResponseDto addHouseNeed(SaveHouseNeedRequestDto houseNeedRequest);
	public GetHouseNeedByIdResponseDto updateHouseNeed(UpdateHouseNeedRequestDto houseNeedRequest);
	public void deleteHouseNeed(DeleteHouseNeedRequestDto houseNeedRequest);

}
