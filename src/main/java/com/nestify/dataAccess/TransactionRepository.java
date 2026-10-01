package com.nestify.dataAccess;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByHouseId(Long houseId);
}
