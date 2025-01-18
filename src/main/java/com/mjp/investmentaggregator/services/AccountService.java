package com.mjp.investmentaggregator.services;

import com.mjp.investmentaggregator.dtos.AccountResponseDTO;
import com.mjp.investmentaggregator.dtos.AccountStockResponseDTO;
import com.mjp.investmentaggregator.dtos.AssociateAccountStockDTO;
import com.mjp.investmentaggregator.entities.AccountStock;
import com.mjp.investmentaggregator.entities.AccountStockId;
import com.mjp.investmentaggregator.repositories.AccountRepository;
import com.mjp.investmentaggregator.repositories.AccountStockRepository;
import com.mjp.investmentaggregator.repositories.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final StockRepository stockRepository;
    private final AccountStockRepository accountStockRepository;

    public AccountService(AccountRepository accountRepository, StockRepository stockRepository, AccountStockRepository accountStockRepository) {
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
        this.accountStockRepository = accountStockRepository;
    }


    public void associateStock(String accountId, AssociateAccountStockDTO dto) {

        var account = accountRepository.findById(UUID.fromString(accountId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var stock = stockRepository.findById(dto.stockId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var id = new AccountStockId(account.getAccountId(), stock.getStockId());
        var entity = new AccountStock(
                id,
                account,
                stock,
                dto.quantity()
        );

        accountStockRepository.save(entity);
    }

    public List<AccountStockResponseDTO> listStocks(String accountId) {
        var account = accountRepository.findById(UUID.fromString(accountId)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
        return account.getAccountStocks().stream().map(as ->
                new AccountStockResponseDTO(as.getStock().getStockId(), as.getQuantity(), 0.0)).toList();
    }
}
