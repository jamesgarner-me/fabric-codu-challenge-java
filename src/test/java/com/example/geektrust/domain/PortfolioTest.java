package com.example.geektrust.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    private Portfolio portfolio;
    private Fund fund1;
    private Fund fund2;
    private Fund fund3;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        
        fund1 = new Fund("FUND1", new HashSet<>(Arrays.asList("STOCK1", "STOCK2")));
        fund2 = new Fund("FUND2", new HashSet<>(Arrays.asList("STOCK3", "STOCK4")));
        fund3 = new Fund("FUND3", new HashSet<>(Arrays.asList("STOCK5")));
    }

    @Test
    @DisplayName("Should create empty portfolio")
    void shouldCreateEmptyPortfolio() {
        assertTrue(portfolio.isEmpty());
        assertEquals(0, portfolio.size());
        assertTrue(portfolio.getCurrentFunds().isEmpty());
    }

    @Test
    @DisplayName("Should set and retrieve current funds")
    void shouldSetAndRetrieveCurrentFunds() {
        List<Fund> funds = Arrays.asList(fund1, fund2);
        
        portfolio.setCurrentFunds(funds);
        
        assertFalse(portfolio.isEmpty());
        assertEquals(2, portfolio.size());
        
        List<Fund> retrievedFunds = portfolio.getCurrentFunds();
        assertEquals(2, retrievedFunds.size());
        assertEquals("FUND1", retrievedFunds.get(0).getName());
        assertEquals("FUND2", retrievedFunds.get(1).getName());
    }

    @Test
    @DisplayName("Should preserve order of funds")
    void shouldPreserveOrderOfFunds() {
        List<Fund> funds = Arrays.asList(fund3, fund1, fund2);
        
        portfolio.setCurrentFunds(funds);
        
        List<Fund> retrievedFunds = portfolio.getCurrentFunds();
        assertEquals("FUND3", retrievedFunds.get(0).getName());
        assertEquals("FUND1", retrievedFunds.get(1).getName());
        assertEquals("FUND2", retrievedFunds.get(2).getName());
    }

    @Test
    @DisplayName("Should update portfolio when setting new funds")
    void shouldUpdatePortfolioWhenSettingNewFunds() {
        List<Fund> initialFunds = Arrays.asList(fund1, fund2);
        portfolio.setCurrentFunds(initialFunds);
        assertEquals(2, portfolio.size());
        
        List<Fund> newFunds = Arrays.asList(fund3);
        portfolio.setCurrentFunds(newFunds);
        
        assertEquals(1, portfolio.size());
        assertEquals("FUND3", portfolio.getCurrentFunds().get(0).getName());
    }

    @Test
    @DisplayName("Should clear portfolio")
    void shouldClearPortfolio() {
        List<Fund> funds = Arrays.asList(fund1, fund2);
        portfolio.setCurrentFunds(funds);
        assertFalse(portfolio.isEmpty());
        
        portfolio.clear();
        
        assertTrue(portfolio.isEmpty());
        assertEquals(0, portfolio.size());
    }

    @Test
    @DisplayName("Should handle empty fund list")
    void shouldHandleEmptyFundList() {
        List<Fund> funds = Arrays.asList(fund1, fund2);
        portfolio.setCurrentFunds(funds);
        assertFalse(portfolio.isEmpty());
        
        portfolio.setCurrentFunds(new ArrayList<>());
        
        assertTrue(portfolio.isEmpty());
        assertEquals(0, portfolio.size());
    }

    @Test
    @DisplayName("Should throw exception for null funds list")
    void shouldThrowExceptionForNullFundsList() {
        assertThrows(NullPointerException.class, () -> {
            portfolio.setCurrentFunds(null);
        });
    }

    @Test
    @DisplayName("Should ensure immutability of returned funds list")
    void shouldEnsureImmutabilityOfReturnedFundsList() {
        List<Fund> funds = Arrays.asList(fund1, fund2);
        portfolio.setCurrentFunds(funds);
        
        List<Fund> retrievedFunds = portfolio.getCurrentFunds();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            retrievedFunds.add(fund3);
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            retrievedFunds.remove(0);
        });
    }

    @Test
    @DisplayName("Should ensure defensive copying of input list")
    void shouldEnsureDefensiveCopyingOfInputList() {
        List<Fund> funds = new ArrayList<>(Arrays.asList(fund1, fund2));
        portfolio.setCurrentFunds(funds);
        
        // Modify original list
        funds.add(fund3);
        
        // Portfolio should not be affected
        assertEquals(2, portfolio.size());
        assertFalse(portfolio.getCurrentFunds().contains(fund3));
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        Portfolio portfolio1 = new Portfolio();
        Portfolio portfolio2 = new Portfolio();
        Portfolio portfolio3 = new Portfolio();
        
        List<Fund> funds1 = Arrays.asList(fund1, fund2);
        List<Fund> funds2 = Arrays.asList(fund1, fund2);
        List<Fund> funds3 = Arrays.asList(fund2, fund1); // Different order
        
        portfolio1.setCurrentFunds(funds1);
        portfolio2.setCurrentFunds(funds2);
        portfolio3.setCurrentFunds(funds3);
        
        // Same instance
        assertEquals(portfolio1, portfolio1);
        
        // Equal portfolios
        assertEquals(portfolio1, portfolio2);
        
        // Different order means different portfolios
        assertNotEquals(portfolio1, portfolio3);
        
        // Empty portfolios are equal
        Portfolio emptyPortfolio1 = new Portfolio();
        Portfolio emptyPortfolio2 = new Portfolio();
        assertEquals(emptyPortfolio1, emptyPortfolio2);
        
        // Null and different type
        assertNotEquals(portfolio1, null);
        assertNotEquals(portfolio1, "STRING");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        Portfolio portfolio1 = new Portfolio();
        Portfolio portfolio2 = new Portfolio();
        
        List<Fund> funds = Arrays.asList(fund1, fund2);
        portfolio1.setCurrentFunds(funds);
        portfolio2.setCurrentFunds(funds);
        
        assertEquals(portfolio1.hashCode(), portfolio2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString")
    void shouldImplementToString() {
        List<Fund> funds = Arrays.asList(fund1);
        portfolio.setCurrentFunds(funds);
        
        String toString = portfolio.toString();
        assertTrue(toString.contains("Portfolio"));
        assertTrue(toString.contains("FUND1"));
    }

    @Test
    @DisplayName("Should handle duplicate funds in list")
    void shouldHandleDuplicateFundsInList() {
        List<Fund> funds = Arrays.asList(fund1, fund1, fund2);
        
        portfolio.setCurrentFunds(funds);
        
        // Should maintain all entries including duplicates
        assertEquals(3, portfolio.size());
        List<Fund> retrievedFunds = portfolio.getCurrentFunds();
        assertEquals(fund1, retrievedFunds.get(0));
        assertEquals(fund1, retrievedFunds.get(1));
        assertEquals(fund2, retrievedFunds.get(2));
    }

    @Test
    @DisplayName("Should be thread-safe for concurrent operations")
    void shouldBeThreadSafeForConcurrentOperations() throws InterruptedException {
        final int numThreads = 10;
        final int operationsPerThread = 100;
        
        Thread[] threads = new Thread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    if (threadId % 2 == 0) {
                        // Even threads set funds
                        portfolio.setCurrentFunds(Arrays.asList(fund1, fund2));
                    } else {
                        // Odd threads read funds
                        portfolio.getCurrentFunds();
                        portfolio.isEmpty();
                        portfolio.size();
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // No assertions needed - test passes if no exceptions are thrown
    }
}