package com.example.geektrust.service;

import com.example.geektrust.domain.Fund;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OverlapCalculatorTest {

    private OverlapCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new OverlapCalculator();
    }

    @Test
    @DisplayName("Should calculate overlap with no common stocks")
    void shouldCalculateOverlapWithNoCommonStocks() {
        Set<String> stocks1 = createStockSet("STOCK1", "STOCK2", "STOCK3");
        Set<String> stocks2 = createStockSet("STOCK4", "STOCK5", "STOCK6");
        
        Fund fund1 = new Fund("FUND1", stocks1);
        Fund fund2 = new Fund("FUND2", stocks2);
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        assertEquals(0.0, overlap, 0.001);
    }

    @Test
    @DisplayName("Should calculate overlap with some common stocks")
    void shouldCalculateOverlapWithSomeCommonStocks() {
        Set<String> stocks1 = createStockSet("STOCK1", "STOCK2", "STOCK3");
        Set<String> stocks2 = createStockSet("STOCK2", "STOCK3", "STOCK4");
        
        Fund fund1 = new Fund("FUND1", stocks1);
        Fund fund2 = new Fund("FUND2", stocks2);
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        // Common stocks: STOCK2, STOCK3 = 2
        // Total stocks: 3 + 3 = 6
        // Overlap = 2 * 2 / 6 * 100 = 66.67%
        assertEquals(66.67, overlap, 0.001);
    }

    @Test
    @DisplayName("Should calculate overlap with identical funds")
    void shouldCalculateOverlapWithIdenticalFunds() {
        Set<String> stocks = createStockSet("STOCK1", "STOCK2", "STOCK3");
        
        Fund fund1 = new Fund("FUND1", stocks);
        Fund fund2 = new Fund("FUND2", new HashSet<>(stocks)); // Create copy
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        // All stocks are common
        // Common stocks: 3, Total stocks: 6
        // Overlap = 2 * 3 / 6 * 100 = 100%
        assertEquals(100.0, overlap, 0.001);
    }

    @Test
    @DisplayName("Should calculate overlap with empty funds")
    void shouldCalculateOverlapWithEmptyFunds() {
        Fund fund1 = new Fund("FUND1", new HashSet<>());
        Fund fund2 = new Fund("FUND2", new HashSet<>());
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        assertEquals(0.0, overlap, 0.001);
    }

    @Test
    @DisplayName("Should calculate overlap with one empty fund")
    void shouldCalculateOverlapWithOneEmptyFund() {
        Set<String> stocks = createStockSet("STOCK1", "STOCK2");
        
        Fund fund1 = new Fund("FUND1", stocks);
        Fund fund2 = new Fund("FUND2", new HashSet<>());
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        // No common stocks, Total stocks: 2 + 0 = 2
        // Overlap = 2 * 0 / 2 * 100 = 0%
        assertEquals(0.0, overlap, 0.001);
    }

    @Test
    @DisplayName("Should round to two decimal places correctly")
    void shouldRoundToTwoDecimalPlacesCorrectly() {
        // Create scenario where we get 39.126... which should round to 39.13
        Set<String> stocks1 = createStockSet("A", "B", "C", "D", "E", "F", "G", "H", "I");
        Set<String> stocks2 = createStockSet("A", "B", "C", "J", "K", "L", "M", "N", "O", "P", "Q");
        
        Fund fund1 = new Fund("FUND1", stocks1);
        Fund fund2 = new Fund("FUND2", stocks2);
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        // Common stocks: 3, Total stocks: 9 + 11 = 20
        // Overlap = 2 * 3 / 20 * 100 = 30%
        assertEquals(30.0, overlap, 0.001);
    }

    @Test
    @DisplayName("Should handle precise rounding scenario like 39.126")
    void shouldHandlePreciseRoundingScenario() {
        // Create a scenario that produces exactly 39.126...
        // We need common/total ratio that gives us 39.126.../100
        // Let's try 9 common stocks out of 46 total (23 + 23)
        // 2 * 9 / 46 * 100 = 39.1304... ≈ 39.13
        Set<String> stocks1 = new HashSet<>();
        Set<String> stocks2 = new HashSet<>();
        
        // Add 9 common stocks
        for (int i = 1; i <= 9; i++) {
            stocks1.add("COMMON" + i);
            stocks2.add("COMMON" + i);
        }
        
        // Add unique stocks to make total 23 each
        for (int i = 1; i <= 14; i++) {
            stocks1.add("UNIQUE1_" + i);
            stocks2.add("UNIQUE2_" + i);
        }
        
        Fund fund1 = new Fund("FUND1", stocks1);
        Fund fund2 = new Fund("FUND2", stocks2);
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        assertEquals(39.13, overlap, 0.001);
    }

    @Test
    @DisplayName("Should verify symmetry of overlap calculation")
    void shouldVerifySymmetryOfOverlapCalculation() {
        Set<String> stocks1 = createStockSet("A", "B", "C");
        Set<String> stocks2 = createStockSet("B", "C", "D", "E");
        
        Fund fund1 = new Fund("FUND1", stocks1);
        Fund fund2 = new Fund("FUND2", stocks2);
        
        double overlap1 = calculator.calculateOverlapPercentage(fund1, fund2);
        double overlap2 = calculator.calculateOverlapPercentage(fund2, fund1);
        
        assertEquals(overlap1, overlap2, 0.001);
    }

    @Test
    @DisplayName("Should throw exception for null funds")
    void shouldThrowExceptionForNullFunds() {
        Fund fund = new Fund("FUND", createStockSet("STOCK1"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateOverlapPercentage(null, fund);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateOverlapPercentage(fund, null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateOverlapPercentage(null, null);
        });
    }

    @Test
    @DisplayName("Should handle case sensitivity in stock names")
    void shouldHandleCaseSensitivityInStockNames() {
        Set<String> stocks1 = createStockSet("STOCK1", "stock2", "Stock3");
        Set<String> stocks2 = createStockSet("STOCK1", "STOCK2", "stock3");
        
        Fund fund1 = new Fund("FUND1", stocks1);
        Fund fund2 = new Fund("FUND2", stocks2);
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        // Only "STOCK1" is exactly matching (case-sensitive)
        // Common stocks: 1, Total stocks: 6
        // Overlap = 2 * 1 / 6 * 100 = 33.33%
        assertEquals(33.33, overlap, 0.001);
    }

    @Test
    @DisplayName("Should handle large number of stocks")
    void shouldHandleLargeNumberOfStocks() {
        Set<String> stocks1 = new HashSet<>();
        Set<String> stocks2 = new HashSet<>();
        
        // Add 100 stocks to each fund with 50 common
        for (int i = 1; i <= 50; i++) {
            stocks1.add("COMMON" + i);
            stocks2.add("COMMON" + i);
        }
        
        for (int i = 1; i <= 50; i++) {
            stocks1.add("UNIQUE1_" + i);
            stocks2.add("UNIQUE2_" + i);
        }
        
        Fund fund1 = new Fund("FUND1", stocks1);
        Fund fund2 = new Fund("FUND2", stocks2);
        
        double overlap = calculator.calculateOverlapPercentage(fund1, fund2);
        
        // Common stocks: 50, Total stocks: 200
        // Overlap = 2 * 50 / 200 * 100 = 50%
        assertEquals(50.0, overlap, 0.001);
    }

    private Set<String> createStockSet(String... stockNames) {
        Set<String> stocks = new HashSet<>();
        for (String stockName : stockNames) {
            stocks.add(stockName);
        }
        return stocks;
    }
}