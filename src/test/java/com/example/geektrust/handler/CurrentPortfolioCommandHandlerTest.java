package com.example.geektrust.handler;

import com.example.geektrust.command.CommandType;
import com.example.geektrust.command.ParsedCommand;
import com.example.geektrust.constants.ErrorMessages;
import com.example.geektrust.domain.Fund;
import com.example.geektrust.domain.Portfolio;
import com.example.geektrust.repository.JsonFundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CurrentPortfolioCommandHandlerTest {

    private CurrentPortfolioCommandHandler handler;
    private Portfolio portfolio;
    private JsonFundRepository fundRepository;

    @BeforeEach
    void setUp() {
        String stockDataPath = getClass().getClassLoader().getResource("stock_data.json").getPath();
        fundRepository = new JsonFundRepository(stockDataPath);
        portfolio = new Portfolio();
        handler = new CurrentPortfolioCommandHandler(portfolio, fundRepository);
    }

    @Test
    @DisplayName("Should set current portfolio funds when all funds exist")
    void shouldSetCurrentPortfolioFundsWhenAllFundsExist() {
        // Given
        ParsedCommand command = ParsedCommand.create(CommandType.CURRENT_PORTFOLIO, 
            Arrays.asList("AXIS_BLUECHIP", "ICICI_PRU_BLUECHIP"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertTrue(result.isSuccess());
        assertFalse(result.hasOutput());
        assertEquals(2, portfolio.size());
        assertTrue(portfolio.getCurrentFundNames().contains("AXIS_BLUECHIP"));
        assertTrue(portfolio.getCurrentFundNames().contains("ICICI_PRU_BLUECHIP"));
    }

    @Test
    @DisplayName("Should return error when fund does not exist")
    void shouldReturnErrorWhenFundDoesNotExist() {
        // Given
        ParsedCommand command = ParsedCommand.create(CommandType.CURRENT_PORTFOLIO, 
            Arrays.asList("AXIS_BLUECHIP", "NON_EXISTENT_FUND"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessages.FUND_NOT_FOUND, result.getErrorMessage());
        assertTrue(portfolio.isEmpty());
    }

    @Test
    @DisplayName("Should handle single fund in portfolio")
    void shouldHandleSingleFundInPortfolio() {
        // Given
        ParsedCommand command = ParsedCommand.create(CommandType.CURRENT_PORTFOLIO, 
            Arrays.asList("AXIS_BLUECHIP"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(1, portfolio.size());
        assertEquals("AXIS_BLUECHIP", portfolio.getCurrentFundNames().get(0));
    }
}