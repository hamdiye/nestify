package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.DeleteEventRequestDto;
import com.nestify.dataTransferObject.request.SaveEventRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventRequestDto;
import com.nestify.dataTransferObject.response.GetEventByIdResponseDto;

public interface EventService {
	public List<GetEventByIdResponseDto> getEventsFromHouse(Long houseId, Long actingUserId);
	public GetEventByIdResponseDto addEvent(SaveEventRequestDto eventRequestDto);
	public GetEventByIdResponseDto updateEvent(Long eventId, UpdateEventRequestDto eventRequestDto);
	public void deleteEvent(DeleteEventRequestDto eventRequestDto);
	
}
