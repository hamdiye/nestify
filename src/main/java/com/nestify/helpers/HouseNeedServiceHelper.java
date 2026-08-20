package com.nestify.helpers;

import org.springframework.stereotype.Component;

import com.nestify.dataAccess.HouseNeedRepository;
import com.nestify.entities.HouseNeed;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class HouseNeedServiceHelper {
	private final HouseNeedRepository houseNeedRepository;
	
    public HouseNeed getHouseNeedOrThrow(Long houseNeedId) {
    	return houseNeedRepository.findById(houseNeedId)
				.orElseThrow(() -> new RuntimeException("İhtiyaç bulunamadı: " + houseNeedId));
    }
	
	
}
