package com.nestify.mapper;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetHouseNeedByIdResponseDto;
import com.nestify.entities.HouseNeed;

@Component
public class HouseNeedMapper {
	public GetHouseNeedByIdResponseDto toGetHouseNeedByIdResponseDto(HouseNeed houseNeed) {
		if(houseNeed == null) {
			return null;
		}
		
		return new GetHouseNeedByIdResponseDto(
					houseNeed.getId(),
					houseNeed.getTitle(),
					houseNeed.getDescription(),
					houseNeed.getCreatedBy().getId(),
					houseNeed.getHouse().getId(),
					houseNeed.getStatus()
				);
		
	}
}
