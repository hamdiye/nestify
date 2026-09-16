package com.nestify.dataAccess;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
	/**
	 * Finds all event categories belonging to a specific house.
	 *
	 * @param houseId ID of the house
	 * @return list of event categories for the house
	 */
	List<EventCategory> findByHouseId(Long houseId);

	/**
	 * Finds the first event category belonging to a specific house.
	 *
	 * @param houseId ID of the house
	 * @return optional containing the first event category if present
	 */
	Optional<EventCategory> findFirstByHouseId(Long houseId);

	/**
	 * Finds an event category for a specific house by its title.
	 *
	 * @param houseId ID of the house
	 * @param title Title of the category
	 * @return optional containing the matching event category if present
	 */
	Optional<EventCategory> findFirstByHouseIdAndTitle(Long houseId, String title);
}
