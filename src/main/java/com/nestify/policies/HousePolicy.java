package com.nestify.policies;

import org.springframework.stereotype.Component;

import com.nestify.entities.House;
import com.nestify.entities.HouseMember;
import com.nestify.helpers.HouseServiceHelper;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class HousePolicy {
	private HouseServiceHelper houseHelper;
	
	public void validateMemberAddition(House house, Long userId, HouseMember actingMember) {
		validateMemberIncludeHouse(house, userId);
		validateAdminAuthority(actingMember);
	}
	public void validateMemberRemoval(House house, Long userId, HouseMember actingMember) {
		boolean isSelfRemoval = actingMember.getUser().getId().equals(userId);
		validateMemberNotIncludeHouse(house, userId);
		if(!isSelfRemoval) {
			validateAdminAuthority(actingMember);
		}
		
		validateLastAdminConstraint(house, userId);
	}
	
	public void validateAdminAuthority(HouseMember member) {
		if(!member.isAdmin()) {
			throw new RuntimeException("Kullanıcının bu işlem için yetkisi bulunmamaktadır.!");
		}
	}
	
	public void validateMemberNotIncludeHouse(House house, Long userId) {
		if(houseHelper.validateUserNotMember(house, userId)) {
			throw new RuntimeException("Kullanıcı bu evin üyesi değil!");
		}
	}
	public void validateMemberIncludeHouse(House house, Long userId) {
		if(!houseHelper.validateUserNotMember(house, userId)) {
			throw new RuntimeException("Kullanıcı bu evin zaten üyesi!");
		}
	}
	
	public void validateLastAdminConstraint(House house, Long userId) {
		HouseMember member = houseHelper.getHouseMember(house, userId);
		if(!member.isAdmin()) {
			return;
		}
		int adminCount = house.getMembers().stream().filter(m -> m.isAdmin()).toList().size();
		
		if(adminCount == 1 &&  house.getMembers().size() > 1) {
			throw new RuntimeException("Evde başka üyeler varken son admin evden ayrılamaz! Lütfen önce başka birini admin yapın.\"");
		}
	}
	public void validateDestroyHouse(House house) {
		int memberSize = houseHelper.getHouseMemberSize(house);
		
		if(memberSize > 0) {
			throw new RuntimeException("Evde üyeler varken evi silemezsiniz!");
		}
	}
	public void validateChangeMemberRole(House house, Long userId, HouseMember actingMember) {
		validateAdminAuthority(actingMember);
		validateMemberNotIncludeHouse(house, userId);
		
		
	}
	
}
