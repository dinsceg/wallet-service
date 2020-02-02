package com.leovegas.wallet.respository;

import com.leovegas.wallet.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    TransactionEntity findByTransactionId(String transactionId);

    List<TransactionEntity> findByAccountNumber(String accountNumber);

}
