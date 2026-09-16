package com.nestify.helpers;

import org.springframework.stereotype.Component;

import com.nestify.dataAccess.EventCategoryRepository;
import com.nestify.entities.EventCategory;
import com.nestify.entities.House;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventCategoryServiceHelper {
	private EventCategoryRepository eventCategoryRepository;
	
	/**
	 * Retrieves an event category by its ID or throws an exception if not found.
	 *
	 * @param eventCategoryId the ID of the event category
	 * @return the EventCategory entity
	 */
	public EventCategory getEventCategoryOrThrow(Long eventCategoryId) {
		return eventCategoryRepository.findById(eventCategoryId)
									  .orElseThrow(() -> new RuntimeException("Event category bulunamadı!"));
	}

	/**
	 * Retrieves an existing default category for the given house, or creates and persists a new default category.
	 *
	 * @param house the house entity
	 * @return default EventCategory
	 */
	public EventCategory getOrCreateDefaultCategory(House house) {
		if (house.getId() != null) {
			return eventCategoryRepository.findFirstByHouseId(house.getId())
					.orElseGet(() -> {
						EventCategory defaultCategory = new EventCategory();
						defaultCategory.setTitle("Genel");
						defaultCategory.setDescription("Genel etkinlikler");
						defaultCategory.setColorCode("#7C3AED");
						defaultCategory.setHouse(house);
						return eventCategoryRepository.save(defaultCategory);
					});
		}

		EventCategory defaultCategory = new EventCategory();
		defaultCategory.setTitle("Genel");
		defaultCategory.setDescription("Genel etkinlikler");
		defaultCategory.setColorCode("#7C3AED");
		defaultCategory.setHouse(house);
		return eventCategoryRepository.save(defaultCategory);
	}
}
