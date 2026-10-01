package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.SaveEventCategoryRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventCategoryRequestDto;
import com.nestify.dataTransferObject.response.GetEventCategoryResponseDto;

public interface EventCategoryService {
	GetEventCategoryResponseDto addEventCategory(Long houseId, Long actingUserId, SaveEventCategoryRequestDto eventCategoryDto);
	GetEventCategoryResponseDto updateEventCategory(Long houseId, Long eventCategoryId, Long actingUserId, UpdateEventCategoryRequestDto updateCategoryDto);
	void deleteEventCategory(Long houseId, Long eventCategoryId, Long actingUserId);
	List<GetEventCategoryResponseDto> getAllEventCategoryFromHouse(Long houseId, Long actingUserId);
}
