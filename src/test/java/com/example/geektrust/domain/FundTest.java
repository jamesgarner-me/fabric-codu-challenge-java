package com.example.geektrust.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FundTest {

    @Test
    @DisplayName("Should create fund with valid data")
    void shouldCreateFundWithValidData() {
        Set<String> stocks = new HashSet<>();
        stocks.add("STOCK1");
        stocks.add("STOCK2");
        
        Fund fund = new Fund("TEST_FUND", stocks);
        
        assertEquals("TEST_FUND", fund.getName());
        assertEquals(2, fund.getStockCount());
        assertTrue(fund.containsStock("STOCK1"));
        assertTrue(fund.containsStock("STOCK2"));
    }

    @Test
    @DisplayName("Should trim fund name")
    void shouldTrimFundName() {
        Set<String> stocks = new HashSet<>();
        Fund fund = new Fund("  TEST_FUND  ", stocks);
        
        assertEquals("TEST_FUND", fund.getName());
    }

    @Test
    @DisplayName("Should create empty fund")
    void shouldCreateEmptyFund() {
        Fund fund = new Fund("EMPTY_FUND", new HashSet<>());
        
        assertEquals("EMPTY_FUND", fund.getName());
        assertEquals(0, fund.getStockCount());
        assertTrue(fund.getStocks().isEmpty());
    }

    @Test
    @DisplayName("Should throw exception for null fund name")
    void shouldThrowExceptionForNullFundName() {
        Set<String> stocks = new HashSet<>();
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Fund(null, stocks);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty fund name")
    void shouldThrowExceptionForEmptyFundName() {
        Set<String> stocks = new HashSet<>();
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Fund("", stocks);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Fund("   ", stocks);
        });
    }

    @Test
    @DisplayName("Should throw exception for null stocks")
    void shouldThrowExceptionForNullStocks() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Fund("TEST_FUND", null);
        });
    }

    @Test
    @DisplayName("Should ensure immutability of stocks")
    void shouldEnsureImmutabilityOfStocks() {
        Set<String> originalStocks = new HashSet<>();
        originalStocks.add("STOCK1");
        
        Fund fund = new Fund("TEST_FUND", originalStocks);
        
        // Modify original set
        originalStocks.add("STOCK2");
        
        // Fund should not be affected
        assertEquals(1, fund.getStockCount());
        assertFalse(fund.containsStock("STOCK2"));
        
        // Try to modify returned set
        assertThrows(UnsupportedOperationException.class, () -> {
            fund.getStocks().add("STOCK3");
        });
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        Set<String> stocks1 = new HashSet<>();
        stocks1.add("STOCK1");
        stocks1.add("STOCK2");
        
        Set<String> stocks2 = new HashSet<>();
        stocks2.add("STOCK1");
        stocks2.add("STOCK2");
        
        Fund fund1 = new Fund("TEST_FUND", stocks1);
        Fund fund2 = new Fund("TEST_FUND", stocks2);
        Fund fund3 = new Fund("OTHER_FUND", stocks1);
        Fund fund4 = new Fund("TEST_FUND", new HashSet<>());
        
        // Same instance
        assertEquals(fund1, fund1);
        
        // Equal funds
        assertEquals(fund1, fund2);
        assertEquals(fund2, fund1);
        
        // Different name
        assertNotEquals(fund1, fund3);
        
        // Different stocks
        assertNotEquals(fund1, fund4);
        
        // Null and different type
        assertNotEquals(fund1, null);
        assertNotEquals(fund1, "STRING");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        Set<String> stocks1 = new HashSet<>();
        stocks1.add("STOCK1");
        stocks1.add("STOCK2");
        
        Set<String> stocks2 = new HashSet<>();
        stocks2.add("STOCK1");
        stocks2.add("STOCK2");
        
        Fund fund1 = new Fund("TEST_FUND", stocks1);
        Fund fund2 = new Fund("TEST_FUND", stocks2);
        
        assertEquals(fund1.hashCode(), fund2.hashCode());
    }

    @Test
    @DisplayName("Should build fund using builder")
    void shouldBuildFundUsingBuilder() {
        Fund fund = new Fund.Builder()
                .withName("BUILDER_FUND")
                .addStock("STOCK1")
                .addStock("STOCK2")
                .addStock("STOCK1") // Duplicate
                .build();
        
        assertEquals("BUILDER_FUND", fund.getName());
        assertEquals(2, fund.getStockCount()); // Duplicate not added
        assertTrue(fund.containsStock("STOCK1"));
        assertTrue(fund.containsStock("STOCK2"));
    }

    @Test
    @DisplayName("Should handle null and empty stocks in builder")
    void shouldHandleNullAndEmptyStocksInBuilder() {
        Fund fund = new Fund.Builder()
                .withName("TEST_FUND")
                .addStock(null)
                .addStock("")
                .addStock("   ")
                .addStock("VALID_STOCK")
                .build();
        
        assertEquals(1, fund.getStockCount());
        assertTrue(fund.containsStock("VALID_STOCK"));
    }

    @Test
    @DisplayName("Should add multiple stocks using builder")
    void shouldAddMultipleStocksUsingBuilder() {
        Set<String> stocks = new HashSet<>();
        stocks.add("STOCK1");
        stocks.add("STOCK2");
        stocks.add(null);
        stocks.add("");
        
        Fund fund = new Fund.Builder()
                .withName("TEST_FUND")
                .addStocks(stocks)
                .build();
        
        assertEquals(2, fund.getStockCount());
        assertTrue(fund.containsStock("STOCK1"));
        assertTrue(fund.containsStock("STOCK2"));
    }

    @Test
    @DisplayName("Should handle null set in builder addStocks")
    void shouldHandleNullSetInBuilderAddStocks() {
        Fund fund = new Fund.Builder()
                .withName("TEST_FUND")
                .addStocks(null)
                .build();
        
        assertEquals(0, fund.getStockCount());
    }

    @Test
    @DisplayName("Should implement toString")
    void shouldImplementToString() {
        Set<String> stocks = new HashSet<>();
        stocks.add("STOCK1");
        
        Fund fund = new Fund("TEST_FUND", stocks);
        String toString = fund.toString();
        
        assertTrue(toString.contains("TEST_FUND"));
        assertTrue(toString.contains("STOCK1"));
    }
}