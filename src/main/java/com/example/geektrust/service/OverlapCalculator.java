package com.example.geektrust.service;

import com.example.geektrust.domain.Fund;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class OverlapCalculator {
    
    private static final int DECIMAL_PLACES = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    
    public double calculateOverlapPercentage(Fund fund1, Fund fund2) {
        if (fund1 == null || fund2 == null) {
            throw new IllegalArgumentException("Funds cannot be null");
        }
        
        Set<String> commonStocks = findCommonStocks(fund1, fund2);
        int totalStocks = fund1.getStockCount() + fund2.getStockCount();
        
        if (totalStocks == 0) {
            return 0.0;
        }
        
        double overlap = (2.0 * commonStocks.size()) / totalStocks * 100;
        return roundToTwoDecimalPlaces(overlap);
    }
    
    private Set<String> findCommonStocks(Fund fund1, Fund fund2) {
        Set<String> common = new HashSet<>(fund1.getStocks());
        common.retainAll(fund2.getStocks());
        return common;
    }
    
    private double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(DECIMAL_PLACES, ROUNDING_MODE);
        return bd.doubleValue();
    }
}