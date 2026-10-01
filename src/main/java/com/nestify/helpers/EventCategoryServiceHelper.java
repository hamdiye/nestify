package com.nestify.helpers;

import org.springframework.stereotype.Component;

import com.nestify.dataAccess.EventCategoryRepository;
import com.nestify.entities.EventCategory;
import com.nestify.entities.House;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventCategoryServiceHelper {
	private final EventCategoryRepository eventCategoryRepository;
	
	public EventCategory getEventCategoryOrThrow(Long eventCategoryId) {
		return eventCategoryRepository.findById(eventCategoryId)
									  .orElseThrow(() -> new RuntimeException("Event category bulunamadı!"));
	}
	
	public EventCategory getOrCreateDefaultCategory(House house) {
	    return eventCategoryRepository.findFirstByHouseId(house.getId())
	            .orElseGet(() -> createDefaultCategory(house));
	}
	
	private EventCategory createDefaultCategory(House house) {
	    EventCategory cat = new EventCategory();
	    cat.setTitle("Genel");
	    cat.setDescription("Genel etkinlikler");
	    cat.setColorCode("#7C3AED");
	    cat.setHouse(house);
	    return eventCategoryRepository.save(cat);
	}
}
