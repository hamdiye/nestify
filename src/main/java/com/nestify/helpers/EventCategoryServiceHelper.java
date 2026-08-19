package com.nestify.helpers;

import org.springframework.stereotype.Component;

import com.nestify.dataAccess.EventCategoryRepository;
import com.nestify.entities.EventCategory;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventCategoryServiceHelper {
	private EventCategoryRepository eventCategoryRepository;
	
	public EventCategory getEventCategoryOrThrow(Long eventCategoryId) {
		return eventCategoryRepository.findById(eventCategoryId)
									  .orElseThrow(() -> new RuntimeException("Event category bulunamadı!"));
	}
}
