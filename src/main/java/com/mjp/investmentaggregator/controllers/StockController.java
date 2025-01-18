package com.mjp.investmentaggregator.controllers;

import com.mjp.investmentaggregator.dtos.CreateStockDTO;
import com.mjp.investmentaggregator.services.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }


    @PostMapping
    public ResponseEntity<Void> createStock(@RequestBody CreateStockDTO createStockDTO) {


        stockService.createStock(createStockDTO);
        return ResponseEntity.ok().build();
    }


}
