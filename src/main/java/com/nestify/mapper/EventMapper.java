package com.nestify.mapper;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetEventByIdResponseDto;
import com.nestify.entities.Event;

@Component
public class EventMapper {
	public GetEventByIdResponseDto toGetEventByIdResponseDto(Event event) {
		if (event == null) {
            return null;
        }
		return new GetEventByIdResponseDto(
				event.getId(),
				event.getTitle(),
				event.getDescription(),
				event.getStartedDate(),
				event.getEndDate(),
				event.getIsAllDay(),
				event.getLocation(),
				event.getEventCategory().getId(),
				event.getHouse().getId(),
				event.getAssignedUser().getId()
			);
	}
}
