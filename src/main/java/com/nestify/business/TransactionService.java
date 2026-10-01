package com.nestify.business;

import java.util.List;

import com.nestify.dataTransferObject.request.SaveTransactionRequestDto;
import com.nestify.dataTransferObject.request.UpdateTransactionRequestDto;
import com.nestify.dataTransferObject.response.GetTransactionByIdResponseDto;

public interface TransactionService {
	public GetTransactionByIdResponseDto addTransaction(Long houseId, Long actingUserId, SaveTransactionRequestDto transactionDto);
	public GetTransactionByIdResponseDto updateTransaction(Long transactionId, Long houseId, Long actingUserId, UpdateTransactionRequestDto transactionDto);
	public List<GetTransactionByIdResponseDto> getTransactionsFromHouse(Long houseId, Long userId);
	public void deleteTransaction(Long houseId, Long transactionId, Long userId);
	
}
