package com.nestify.dataTransferObject.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nestify.entities.enums.TransactionCategory;
import com.nestify.entities.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionByIdResponseDto {
	private Long id;
	private String title;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionCategory category;
    private LocalDate transactionDate;
    private Long userId;
}
