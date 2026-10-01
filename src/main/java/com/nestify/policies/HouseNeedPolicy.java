package com.nestify.policies;

import org.springframework.stereotype.Component;

import com.nestify.entities.House;
import com.nestify.entities.HouseNeed;
import com.nestify.helpers.HouseServiceHelper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class HouseNeedPolicy {

	private final HouseServiceHelper houseHelper;

	public void validateHouseNeedOperation(House house, Long userId) {
		if (houseHelper.isNotMember(house, userId)) {
			throw new RuntimeException("Kullanıcı bu evin üyesi değil!");
		}
	}
	
	public void validateHouseNeedBelogsToHouse(HouseNeed houseNeed, Long houseId) {
		if(!houseNeed.getHouse().getId().equals(houseId)) {
			throw new RuntimeException("Bu ihtiyaç belirtilen eve ait değil!");
		}
	}
}
