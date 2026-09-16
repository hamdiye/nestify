package com.nestify.mapper;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetEventByIdResponseDto;
import com.nestify.entities.Event;

@Component
public class EventMapper {
	/**
	 * Converts an Event entity to a GetEventByIdResponseDto.
	 *
	 * @param event the Event entity to convert
	 * @return the converted GetEventByIdResponseDto or null if input is null
	 */
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
				event.getEventCategory() != null ? event.getEventCategory().getId() : null,
				event.getHouse() != null ? event.getHouse().getId() : null,
				event.getAssignedUser() != null ? event.getAssignedUser().getId() : null
			);
	}
}
