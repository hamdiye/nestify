package com.nestify.business;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nestify.dataAccess.TransactionRepository;
import com.nestify.dataTransferObject.request.SaveTransactionRequestDto;
import com.nestify.dataTransferObject.request.UpdateTransactionRequestDto;
import com.nestify.dataTransferObject.response.GetTransactionByIdResponseDto;
import com.nestify.entities.House;
import com.nestify.entities.Transaction;
import com.nestify.entities.User;
import com.nestify.helpers.HouseServiceHelper;
import com.nestify.helpers.TransactionServiceHelper;
import com.nestify.helpers.UserServiceHelper;
import com.nestify.mapper.TransactionMapper;
import com.nestify.policies.TransactionPolicy;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service("nestifyTransactionManager")
public class TransactionManager implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final UserServiceHelper userServiceHelper;
	private final HouseServiceHelper houseServiceHelper;
	private final TransactionServiceHelper transactionServiceHelper;
	private final TransactionMapper transactionMapper;
	private final TransactionPolicy transactionPolicy;

	@Override
	public GetTransactionByIdResponseDto addTransaction(Long houseId, Long actingUserId,
			SaveTransactionRequestDto transactionDto) {
		User user = userServiceHelper.getUserOrThrow(transactionDto.getUserId());
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		transactionPolicy.validateTransactionOperation(house, user.getId());
		transactionPolicy.validateTransactionOperation(house, actingUserId);

		Transaction transaction = new Transaction();
		transaction.setTitle(transactionDto.getTitle());
		transaction.setAmount(transactionDto.getAmount());
		transaction.setCategory(transactionDto.getCategory());
		transaction.setType(transactionDto.getType());
		transaction.setUser(user);
		transaction.setHouse(house);

		Transaction savedTransaction = transactionRepository.save(transaction);

		return transactionMapper.toGetTransactionByIdResponseDto(savedTransaction);
	}

	@Override
	public GetTransactionByIdResponseDto updateTransaction(Long transactionId,
			Long houseId,
			Long actingUserId,
			UpdateTransactionRequestDto transactionDto) {
		Transaction transaction = transactionServiceHelper.getTransactionOrThrow(transactionId);
		transactionPolicy.validateTransactionBelogsToHouse(transaction, houseId);

		House house = houseServiceHelper.getHouseOrThrow(houseId);
		User user = userServiceHelper.getUserOrThrow(transactionDto.getUserId());
		transactionPolicy.validateTransactionOperation(house, transactionDto.getUserId());
		transactionPolicy.validateTransactionOperation(house, actingUserId);

		transaction.setTitle(transactionDto.getTitle());
		transaction.setAmount(transactionDto.getAmount());
		transaction.setCategory(transactionDto.getCategory());
		transaction.setType(transactionDto.getType());
		transaction.setUser(user);

		Transaction savedTransaction = transactionRepository.save(transaction);

		return transactionMapper.toGetTransactionByIdResponseDto(savedTransaction);
	}

	@Override
	public List<GetTransactionByIdResponseDto> getTransactionsFromHouse(Long houseId, Long actingUserId) {
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		transactionPolicy.validateTransactionOperation(house, actingUserId);
		List<Transaction> transactions = transactionRepository.findByHouseId(houseId);
		return transactions.stream().map(transactionMapper::toGetTransactionByIdResponseDto).toList();
	}

	@Override
	public void deleteTransaction(Long houseId, Long transactionId, Long actingUserId) {
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		transactionPolicy.validateTransactionOperation(house, actingUserId);

		Transaction transaction = transactionServiceHelper.getTransactionOrThrow(transactionId);
		transactionPolicy.validateTransactionBelogsToHouse(transaction, houseId);

		transactionRepository.delete(transaction);
	}

}
