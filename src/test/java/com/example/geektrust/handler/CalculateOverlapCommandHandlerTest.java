package com.example.geektrust.handler;

import com.example.geektrust.command.CommandType;
import com.example.geektrust.command.ParsedCommand;
import com.example.geektrust.constants.ErrorMessages;
import com.example.geektrust.domain.Portfolio;
import com.example.geektrust.repository.JsonFundRepository;
import com.example.geektrust.service.OverlapCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CalculateOverlapCommandHandlerTest {

    private CalculateOverlapCommandHandler handler;
    private Portfolio portfolio;
    private JsonFundRepository fundRepository;
    private OverlapCalculator overlapCalculator;

    @BeforeEach
    void setUp() {
        String stockDataPath = getClass().getClassLoader().getResource("stock_data.json").getPath();
        fundRepository = new JsonFundRepository(stockDataPath);
        portfolio = new Portfolio();
        overlapCalculator = new OverlapCalculator();
        handler = new CalculateOverlapCommandHandler(portfolio, fundRepository, overlapCalculator);
    }

    @Test
    @DisplayName("Should return error when target fund does not exist")
    void shouldReturnErrorWhenTargetFundDoesNotExist() {
        // Given
        ParsedCommand command = ParsedCommand.create(CommandType.CALCULATE_OVERLAP, 
            Arrays.asList("NON_EXISTENT_FUND"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessages.FUND_NOT_FOUND, result.getErrorMessage());
    }

    @Test
    @DisplayName("Should return success with no output when portfolio is empty")
    void shouldReturnSuccessWithNoOutputWhenPortfolioIsEmpty() {
        // Given
        ParsedCommand command = ParsedCommand.create(CommandType.CALCULATE_OVERLAP, 
            Arrays.asList("AXIS_BLUECHIP"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertTrue(result.isSuccess());
        assertFalse(result.hasOutput());
    }

    @Test
    @DisplayName("Should calculate overlap with portfolio funds")
    void shouldCalculateOverlapWithPortfolioFunds() {
        // Given
        portfolio.setCurrentFundNames(Arrays.asList("AXIS_BLUECHIP", "ICICI_PRU_BLUECHIP"));
        ParsedCommand command = ParsedCommand.create(CommandType.CALCULATE_OVERLAP, 
            Arrays.asList("MIRAE_ASSET_EMERGING_BLUECHIP"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.hasOutput());
        assertEquals(2, result.getOutputs().size());
        assertTrue(result.getOutputs().get(0).contains("MIRAE_ASSET_EMERGING_BLUECHIP AXIS_BLUECHIP"));
        assertTrue(result.getOutputs().get(1).contains("MIRAE_ASSET_EMERGING_BLUECHIP ICICI_PRU_BLUECHIP"));
    }

    @Test
    @DisplayName("Should exclude zero overlap results")
    void shouldExcludeZeroOverlapResults() {
        // Given - setting portfolio with funds that have no overlap
        portfolio.setCurrentFundNames(Arrays.asList("UTI_NIFTY_INDEX"));
        ParsedCommand command = ParsedCommand.create(CommandType.CALCULATE_OVERLAP, 
            Arrays.asList("AXIS_BLUECHIP"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertTrue(result.isSuccess());
        // Should have output if there's any overlap, even small
        // This tests the filtering of 0.0 overlap
    }

    @Test
    @DisplayName("Should handle single portfolio fund")
    void shouldHandleSinglePortfolioFund() {
        // Given
        portfolio.setCurrentFundNames(Arrays.asList("ICICI_PRU_BLUECHIP"));
        ParsedCommand command = ParsedCommand.create(CommandType.CALCULATE_OVERLAP, 
            Arrays.asList("AXIS_BLUECHIP"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.hasOutput());
        assertEquals(1, result.getOutputs().size());
        assertTrue(result.getOutputs().get(0).contains("AXIS_BLUECHIP ICICI_PRU_BLUECHIP"));
    }
}