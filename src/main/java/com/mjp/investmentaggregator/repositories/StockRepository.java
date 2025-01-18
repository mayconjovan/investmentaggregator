package com.mjp.investmentaggregator.repositories;

import com.mjp.investmentaggregator.entities.Account;
import com.mjp.investmentaggregator.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

}
