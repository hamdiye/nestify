package com.nestify.dataAccess;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.EventCategory;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
	List<EventCategory> findByHouseId(Long houseId);

	Optional<EventCategory> findFirstByHouseId(Long houseId);

	Optional<EventCategory> findFirstByHouseIdAndTitle(Long houseId, String title);
}
