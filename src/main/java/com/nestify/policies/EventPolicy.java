package com.nestify.policies;

import org.springframework.stereotype.Component;

import com.nestify.entities.House;
import com.nestify.helpers.HouseServiceHelper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventPolicy {
	private HouseServiceHelper houseHelper;
	
	public void validateEventOperation(House house,Long userId) {
		if(houseHelper.validateUserNotMember(house, userId)) {
			throw new RuntimeException("Kullanıcı bu evin üyesi değil!");
		}
	}
}
