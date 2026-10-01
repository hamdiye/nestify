package com.nestify.policies;

import org.springframework.stereotype.Component;

import com.nestify.entities.Transaction;
import com.nestify.entities.House;
import com.nestify.helpers.HouseServiceHelper;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TransactionPolicy {
	private HouseServiceHelper houseHelper;

	public void validateTransactionOperation(House house, Long userId) {
		if (houseHelper.isNotMember(house, userId)) {
			throw new RuntimeException("Kullanıcı bu evin üyesi değil!");
		}
	}

	public void validateTransactionBelogsToHouse(Transaction transaction, Long houseId) {
		if(!transaction.getHouse().getId().equals(houseId)) {
			throw new RuntimeException("Bu gider belirtilen eve ait değil!");
		}
	}
}
