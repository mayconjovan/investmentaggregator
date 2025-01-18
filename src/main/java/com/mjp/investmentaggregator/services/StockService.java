package com.mjp.investmentaggregator.services;

import com.mjp.investmentaggregator.dtos.CreateStockDTO;
import com.mjp.investmentaggregator.entities.Stock;
import com.mjp.investmentaggregator.repositories.StockRepository;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public void createStock(CreateStockDTO createStockDTO) {
        var stock = new Stock(createStockDTO.stockId(), createStockDTO.description());

        stockRepository.save(stock);
    }
}
