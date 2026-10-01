package com.nestify.mapper;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetTransactionByIdResponseDto;
import com.nestify.entities.Transaction;

@Component
public class TransactionMapper {
	public GetTransactionByIdResponseDto toGetTransactionByIdResponseDto(Transaction transaction) {
		if (transaction == null) {
			return null;
		}

		return new GetTransactionByIdResponseDto(
				transaction.getId(),
				transaction.getTitle(),
				transaction.getAmount(),
				transaction.getType(),
				transaction.getCategory(),
				transaction.getTransactionDate(),
				transaction.getUser() != null ? transaction.getUser().getId() : null);
	}
}
