package com.nestify.policies;

import org.springframework.stereotype.Component;

import com.nestify.entities.House;
import com.nestify.helpers.HouseServiceHelper;
import com.nestify.helpers.UserServiceHelper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class HouseNeedPolicy {

	private HouseServiceHelper houseHelper;
	private UserServiceHelper userServiceHelper;

	/**
	 * Validates that the user is a member of the given house.
	 *
	 * @param house  the house to check membership against
	 * @param userId the ID of the acting user
	 * @throws RuntimeException if the user is not a member of the house
	 */
	public void validateHouseNeedOperation(House house, Long userId) {
		if (houseHelper.validateUserNotMember(house, userId)) {
			throw new RuntimeException("Kullanıcı bu evin üyesi değil!");
		}
	}

	/**
	 * Validates that the user is a member of at least one house before creating a house need.
	 *
	 * @param userId the ID of the acting user
	 * @throws RuntimeException if the user is not a member of any house
	 */
	public void validateHouseNeedCreation(Long userId) {
		if (!userServiceHelper.isUserMemberOfAnyHouse(userId)) {
			throw new RuntimeException("İhtiyaç oluşturmak için en az bir eve üye olmanız gerekiyor.");
		}
	}
}
