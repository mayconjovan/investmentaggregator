package com.mjp.investmentaggregator.repositories;

import com.mjp.investmentaggregator.entities.Account;
import com.mjp.investmentaggregator.entities.AccountStock;
import com.mjp.investmentaggregator.entities.AccountStockId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountStockRepository extends JpaRepository<AccountStock, AccountStockId> {

}
