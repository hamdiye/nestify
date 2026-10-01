package com.nestify.api;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.TransactionService;
import com.nestify.core.UserPrincipal;
import com.nestify.dataTransferObject.request.SaveTransactionRequestDto;
import com.nestify.dataTransferObject.request.UpdateTransactionRequestDto;
import com.nestify.dataTransferObject.response.GetTransactionByIdResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/houses")
@AllArgsConstructor
public class TransactionController {
	private final TransactionService transactionService;

	@PostMapping("/{houseId}/transactions")
	public GetTransactionByIdResponseDto addTransaction(@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody SaveTransactionRequestDto transactionDto) {
		return transactionService.addTransaction(houseId, currentUser.getId(), transactionDto);
	}

	@PutMapping("/{houseId}/transactions/{transactionId}")
	public GetTransactionByIdResponseDto updateTransaction(@PathVariable Long transactionId,
			@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody UpdateTransactionRequestDto transactionDto) {
		return transactionService.updateTransaction(transactionId, houseId, currentUser.getId(), transactionDto);

	}

	@DeleteMapping("/{houseId}/transactions/{transactionId}")
	public void deleteTransaction(@PathVariable Long transactionId,
			@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		transactionService.deleteTransaction(houseId, transactionId, currentUser.getId());
	}
	
	@GetMapping("/{houseId}/transactions")
	public List<GetTransactionByIdResponseDto> getTransactionsFromHouse(@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser){
		return transactionService.getTransactionsFromHouse(houseId, currentUser.getId());
	}
}
