package com.leovegas.wallet.respository;

import com.leovegas.wallet.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    AccountEntity findByAccountNumber(String accountNumber);

}
