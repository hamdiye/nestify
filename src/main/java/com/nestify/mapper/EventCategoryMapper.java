package com.nestify.mapper;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetEventCategoryResponseDto;
import com.nestify.entities.EventCategory;

@Component
public class EventCategoryMapper {
	public GetEventCategoryResponseDto toGetEventCategoryResponseDto(EventCategory eventCategory) {
		if (eventCategory == null) {
            return null;
        }
		return new GetEventCategoryResponseDto(
				eventCategory.getId(),
				eventCategory.getHouse().getId(),
				eventCategory.getTitle(),
				eventCategory.getDescription(),
				eventCategory.getColorCode()
				);
	}
}
