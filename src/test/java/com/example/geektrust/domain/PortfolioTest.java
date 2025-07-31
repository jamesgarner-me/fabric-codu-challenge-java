package com.example.geektrust.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
    }

    @Test
    @DisplayName("Should create empty portfolio")
    void shouldCreateEmptyPortfolio() {
        assertTrue(portfolio.isEmpty());
        assertEquals(0, portfolio.size());
        assertTrue(portfolio.getCurrentFundNames().isEmpty());
    }

    @Test
    @DisplayName("Should set and retrieve current fund names")
    void shouldSetAndRetrieveCurrentFundNames() {
        List<String> fundNames = Arrays.asList("FUND1", "FUND2");
        
        portfolio.setCurrentFundNames(fundNames);
        
        assertFalse(portfolio.isEmpty());
        assertEquals(2, portfolio.size());
        
        List<String> retrievedFundNames = portfolio.getCurrentFundNames();
        assertEquals(2, retrievedFundNames.size());
        assertEquals("FUND1", retrievedFundNames.get(0));
        assertEquals("FUND2", retrievedFundNames.get(1));
    }

    @Test
    @DisplayName("Should preserve order of fund names")
    void shouldPreserveOrderOfFundNames() {
        List<String> fundNames = Arrays.asList("FUND3", "FUND1", "FUND2");
        
        portfolio.setCurrentFundNames(fundNames);
        
        List<String> retrievedFundNames = portfolio.getCurrentFundNames();
        assertEquals("FUND3", retrievedFundNames.get(0));
        assertEquals("FUND1", retrievedFundNames.get(1));
        assertEquals("FUND2", retrievedFundNames.get(2));
    }

    @Test
    @DisplayName("Should update portfolio when setting new fund names")
    void shouldUpdatePortfolioWhenSettingNewFundNames() {
        List<String> initialFundNames = Arrays.asList("FUND1", "FUND2");
        portfolio.setCurrentFundNames(initialFundNames);
        assertEquals(2, portfolio.size());
        
        List<String> newFundNames = Arrays.asList("FUND3");
        portfolio.setCurrentFundNames(newFundNames);
        
        assertEquals(1, portfolio.size());
        assertEquals("FUND3", portfolio.getCurrentFundNames().get(0));
    }

    @Test
    @DisplayName("Should clear portfolio")
    void shouldClearPortfolio() {
        List<String> fundNames = Arrays.asList("FUND1", "FUND2");
        portfolio.setCurrentFundNames(fundNames);
        assertFalse(portfolio.isEmpty());
        
        portfolio.clear();
        
        assertTrue(portfolio.isEmpty());
        assertEquals(0, portfolio.size());
    }

    @Test
    @DisplayName("Should handle empty fund name list")
    void shouldHandleEmptyFundNameList() {
        List<String> fundNames = Arrays.asList("FUND1", "FUND2");
        portfolio.setCurrentFundNames(fundNames);
        assertFalse(portfolio.isEmpty());
        
        portfolio.setCurrentFundNames(new ArrayList<>());
        
        assertTrue(portfolio.isEmpty());
        assertEquals(0, portfolio.size());
    }

    @Test
    @DisplayName("Should throw exception for null fund names list")
    void shouldThrowExceptionForNullFundNamesList() {
        assertThrows(NullPointerException.class, () -> {
            portfolio.setCurrentFundNames(null);
        });
    }

    @Test
    @DisplayName("Should ensure immutability of returned fund names list")
    void shouldEnsureImmutabilityOfReturnedFundNamesList() {
        List<String> fundNames = Arrays.asList("FUND1", "FUND2");
        portfolio.setCurrentFundNames(fundNames);
        
        List<String> retrievedFundNames = portfolio.getCurrentFundNames();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            retrievedFundNames.add("FUND3");
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            retrievedFundNames.remove(0);
        });
    }

    @Test
    @DisplayName("Should ensure defensive copying of input list")
    void shouldEnsureDefensiveCopyingOfInputList() {
        List<String> fundNames = new ArrayList<>(Arrays.asList("FUND1", "FUND2"));
        portfolio.setCurrentFundNames(fundNames);
        
        // Modify original list
        fundNames.add("FUND3");
        
        // Portfolio should not be affected
        assertEquals(2, portfolio.size());
        assertFalse(portfolio.getCurrentFundNames().contains("FUND3"));
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        Portfolio portfolio1 = new Portfolio();
        Portfolio portfolio2 = new Portfolio();
        Portfolio portfolio3 = new Portfolio();
        
        List<String> fundNames1 = Arrays.asList("FUND1", "FUND2");
        List<String> fundNames2 = Arrays.asList("FUND1", "FUND2");
        List<String> fundNames3 = Arrays.asList("FUND2", "FUND1"); // Different order
        
        portfolio1.setCurrentFundNames(fundNames1);
        portfolio2.setCurrentFundNames(fundNames2);
        portfolio3.setCurrentFundNames(fundNames3);
        
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
        
        List<String> fundNames = Arrays.asList("FUND1", "FUND2");
        portfolio1.setCurrentFundNames(fundNames);
        portfolio2.setCurrentFundNames(fundNames);
        
        assertEquals(portfolio1.hashCode(), portfolio2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString")
    void shouldImplementToString() {
        List<String> fundNames = Arrays.asList("FUND1");
        portfolio.setCurrentFundNames(fundNames);
        
        String toString = portfolio.toString();
        assertTrue(toString.contains("Portfolio"));
        assertTrue(toString.contains("FUND1"));
    }
}