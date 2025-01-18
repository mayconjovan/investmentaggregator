package com.mjp.investmentaggregator.services;

import com.mjp.investmentaggregator.client.BrapiClient;
import com.mjp.investmentaggregator.dtos.AccountResponseDTO;
import com.mjp.investmentaggregator.dtos.AccountStockResponseDTO;
import com.mjp.investmentaggregator.dtos.AssociateAccountStockDTO;
import com.mjp.investmentaggregator.dtos.BrapiResponseDTO;
import com.mjp.investmentaggregator.entities.AccountStock;
import com.mjp.investmentaggregator.entities.AccountStockId;
import com.mjp.investmentaggregator.repositories.AccountRepository;
import com.mjp.investmentaggregator.repositories.AccountStockRepository;
import com.mjp.investmentaggregator.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Value("#{environment.TOKEN}")
    private String TOKEN;

    private final AccountRepository accountRepository;
    private final StockRepository stockRepository;
    private final AccountStockRepository accountStockRepository;
    private final BrapiClient brapiClient;

    public AccountService(AccountRepository accountRepository, StockRepository stockRepository, AccountStockRepository accountStockRepository, BrapiClient brapiClient) {
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
        this.accountStockRepository = accountStockRepository;
        this.brapiClient = brapiClient;
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
                new AccountStockResponseDTO(
                        as.getStock().getStockId(),
                        as.getQuantity(),
                        getTotal(as.getQuantity(), as.getStock().getStockId())))
                .toList();
    }

    private double getTotal(Integer quantity, String stockId) {
        var response = brapiClient.getQuote(TOKEN, stockId);
        var price = response.results().getFirst().regularMarketPrice();

        return quantity * price;
    }
}
