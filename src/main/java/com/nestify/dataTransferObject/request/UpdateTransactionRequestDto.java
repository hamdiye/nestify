package com.nestify.dataTransferObject.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nestify.entities.enums.TransactionCategory;
import com.nestify.entities.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTransactionRequestDto {
	private String title;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionCategory category;
    private LocalDate transactionDate;
    private Long userId;
    private Long houseId;
}
