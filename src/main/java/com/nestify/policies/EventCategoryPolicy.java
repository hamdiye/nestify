package com.nestify.policies;

import org.springframework.stereotype.Component;

import com.nestify.entities.EventCategory;
import com.nestify.entities.House;
import com.nestify.helpers.HouseServiceHelper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventCategoryPolicy {
	private final HouseServiceHelper houseHelper;
	
	public void validateEventCategoryOperation(House house,Long userId) {
		if(houseHelper.isNotMember(house, userId)) {
			throw new RuntimeException("Kullanıcı bu evin üyesi değil!");
		}
	}
	
	public void validateEventCategoryBelogsToHouse(EventCategory eventCategory, Long houseId) {
		if(!eventCategory.getHouse().getId().equals(houseId)) {
			throw new RuntimeException("Bu etkinlik kategorisi belirtilen eve ait değil!");
		}
	}
	
}
