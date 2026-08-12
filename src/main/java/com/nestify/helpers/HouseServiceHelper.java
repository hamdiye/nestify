package com.nestify.helpers;

import org.springframework.stereotype.Component;

import com.nestify.dataAccess.HouseRepository;
import com.nestify.entities.House;
import com.nestify.entities.HouseMember;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class HouseServiceHelper {
	private final HouseRepository houseRepository;
	
    public House getHouseOrThrow(Long houseId) {
    	return houseRepository.findById(houseId)
				.orElseThrow(() -> new RuntimeException("Ev bulunamadı: " + houseId));
    }
    
    public HouseMember getHouseMember(House house, Long userId) {
    	return house.getMembers().stream().filter(m -> m.getUser().getId().equals(userId))
										  .findFirst()
										  .orElseThrow(() -> new RuntimeException("Kullanıcı bu evin üyesi değil!"));
    }
    
    public boolean validateUserNotMember(House house, Long userId) {
    	return !house.getMembers().stream().anyMatch(member -> member.getUser().getId().equals(userId));
    }
    
    public int getHouseMemberSize(House house) {
    	return house.getMembers().size();
    }

}
