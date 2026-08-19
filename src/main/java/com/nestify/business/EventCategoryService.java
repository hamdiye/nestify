package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.DeleteEventCategoryRequestDto;
import com.nestify.dataTransferObject.request.SaveEventCategoryRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventCategoryRequestDto;
import com.nestify.dataTransferObject.response.GetEventCategoryResponseDto;

public interface EventCategoryService {
	public GetEventCategoryResponseDto addEventCategory(SaveEventCategoryRequestDto eventCategoryDto);
	public GetEventCategoryResponseDto updateEventCategory(Long eventCategoryId, UpdateEventCategoryRequestDto updateCategoryDto);
	public void deleteEventCategory(DeleteEventCategoryRequestDto deleteEventRequest);
	public List<GetEventCategoryResponseDto> getAllEventCategoryFromHouse(Long houseId, Long actingUserId);
}
