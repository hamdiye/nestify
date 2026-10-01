package com.nestify.helpers;

import org.springframework.stereotype.Component;

import com.nestify.dataAccess.TransactionRepository;
import com.nestify.entities.Transaction;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TransactionServiceHelper {
	private final TransactionRepository transactionRepository;
		
	public Transaction getTransactionOrThrow(Long transactionId) {
    	return transactionRepository.findById(transactionId)
				.orElseThrow(() -> new RuntimeException("Gider bulunamadı: " + transactionId));
    }
}
