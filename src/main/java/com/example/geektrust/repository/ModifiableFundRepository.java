package com.example.geektrust.repository;

import com.example.geektrust.domain.Fund;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ModifiableFundRepository implements FundRepository {
    private final FundRepository delegate;
    private final Map<String, Set<String>> stockModifications = new ConcurrentHashMap<>();

    public ModifiableFundRepository(FundRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<Fund> getFundByName(String fundName) {
        Optional<Fund> originalFund = delegate.getFundByName(fundName);
        if (!originalFund.isPresent()) {
            return Optional.empty();
        }

        Fund fund = originalFund.get();
        Set<String> additionalStocks = stockModifications.get(fundName);
        
        if (additionalStocks == null || additionalStocks.isEmpty()) {
            return originalFund;
        }

        Set<String> modifiedStocks = new HashSet<>(fund.getStocks());
        modifiedStocks.addAll(additionalStocks);
        
        return Optional.of(new Fund(fund.getName(), modifiedStocks));
    }

    @Override
    public List<Fund> getAllFunds() {
        return delegate.getAllFunds();
    }

    public void addStockToFund(String fundName, String stockName) {
        stockModifications.computeIfAbsent(fundName, k -> new HashSet<>()).add(stockName);
    }
}