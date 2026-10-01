package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.SaveEventRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventRequestDto;
import com.nestify.dataTransferObject.response.GetEventByIdResponseDto;

public interface EventService {
	public List<GetEventByIdResponseDto> getEventsFromHouse(Long houseId, Long actingUserId);
	public GetEventByIdResponseDto addEvent(Long houseId, Long actingUserId, SaveEventRequestDto eventRequestDto);
	public GetEventByIdResponseDto updateEvent(Long houseId, Long eventId, Long actingUserId, UpdateEventRequestDto eventRequestDto);
	public void deleteEvent(Long houseId, Long eventId, Long actingUserId);
	
}
