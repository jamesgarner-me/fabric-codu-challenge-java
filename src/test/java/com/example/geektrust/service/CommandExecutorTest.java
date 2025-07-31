package com.example.geektrust.service;

import com.example.geektrust.command.CommandType;
import com.example.geektrust.domain.Portfolio;
import com.example.geektrust.handler.*;
import com.example.geektrust.repository.JsonFundRepository;
import com.example.geektrust.repository.ModifiableFundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutorTest {

    private CommandExecutor commandExecutor;

    @BeforeEach
    void setUp() {
        String stockDataPath = getClass().getClassLoader().getResource("stock_data.json").getPath();
        JsonFundRepository jsonRepository = new JsonFundRepository(stockDataPath);
        ModifiableFundRepository modifiableRepository = new ModifiableFundRepository(jsonRepository);
        Portfolio portfolio = new Portfolio();
        OverlapCalculator overlapCalculator = new OverlapCalculator();

        commandExecutor = new CommandExecutor();
        commandExecutor.registerHandler(CommandType.CURRENT_PORTFOLIO, 
            new CurrentPortfolioCommandHandler(portfolio, modifiableRepository));
        commandExecutor.registerHandler(CommandType.CALCULATE_OVERLAP, 
            new CalculateOverlapCommandHandler(portfolio, modifiableRepository, overlapCalculator));
        commandExecutor.registerHandler(CommandType.ADD_STOCK, 
            new AddStockCommandHandler(modifiableRepository));
    }

    @Test
    @DisplayName("Should execute command successfully")
    void shouldExecuteCommandSuccessfully() {
        // Given
        List<String> commands = Arrays.asList("CURRENT_PORTFOLIO AXIS_BLUECHIP ICICI_PRU_BLUECHIP");

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertEquals(1, results.size());
        assertTrue(results.get(0).isSuccess());
    }

    @Test
    @DisplayName("Should handle multiple commands")
    void shouldHandleMultipleCommands() {
        // Given
        List<String> commands = Arrays.asList(
            "CURRENT_PORTFOLIO AXIS_BLUECHIP ICICI_PRU_BLUECHIP",
            "CALCULATE_OVERLAP MIRAE_ASSET_EMERGING_BLUECHIP"
        );

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertEquals(2, results.size());
        assertTrue(results.get(0).isSuccess());
        assertTrue(results.get(1).isSuccess());
        assertTrue(results.get(1).hasOutput());
    }

    @Test
    @DisplayName("Should return error when no handler found")
    void shouldReturnErrorWhenNoHandlerFound() {
        // Given
        CommandExecutor emptyExecutor = new CommandExecutor();
        List<String> commands = Arrays.asList("CURRENT_PORTFOLIO AXIS_BLUECHIP");

        // When
        List<CommandResult> results = emptyExecutor.executeCommands(commands);

        // Then
        assertEquals(1, results.size());
        assertFalse(results.get(0).isSuccess());
        assertTrue(results.get(0).getErrorMessage().contains("No handler found for command:"));
    }

    @Test
    @DisplayName("Should handle invalid command format")
    void shouldHandleInvalidCommandFormat() {
        // Given
        List<String> commands = Arrays.asList("INVALID_COMMAND");

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertEquals(1, results.size());
        assertFalse(results.get(0).isSuccess());
        assertTrue(results.get(0).getErrorMessage().contains("Invalid command:"));
    }

    @Test
    @DisplayName("Should handle empty command")
    void shouldHandleEmptyCommand() {
        // Given
        List<String> commands = Arrays.asList("");

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertEquals(1, results.size());
        assertFalse(results.get(0).isSuccess());
        assertTrue(results.get(0).getErrorMessage().contains("Invalid command:"));
    }

    @Test
    @DisplayName("Should handle null command")
    void shouldHandleNullCommand() {
        // Given
        List<String> commands = Arrays.asList((String) null);

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertEquals(1, results.size());
        assertFalse(results.get(0).isSuccess());
        assertTrue(results.get(0).getErrorMessage().contains("Invalid command:"));
    }

    @Test
    @DisplayName("Should handle empty command list")
    void shouldHandleEmptyCommandList() {
        // Given
        List<String> commands = Collections.emptyList();

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should continue processing after error")
    void shouldContinueProcessingAfterError() {
        // Given
        List<String> commands = Arrays.asList(
            "INVALID_COMMAND",
            "CURRENT_PORTFOLIO AXIS_BLUECHIP"
        );

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertEquals(2, results.size());
        assertFalse(results.get(0).isSuccess());
        assertTrue(results.get(1).isSuccess());
    }

    @Test
    @DisplayName("Should handle ADD_STOCK command")
    void shouldHandleAddStockCommand() {
        // Given
        List<String> commands = Arrays.asList(
            "ADD_STOCK AXIS_BLUECHIP NEW_STOCK"
        );

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertEquals(1, results.size());
        assertTrue(results.get(0).isSuccess());
    }

    @Test
    @DisplayName("Should handle whitespace-only command")
    void shouldHandleWhitespaceOnlyCommand() {
        // Given
        List<String> commands = Arrays.asList("   ");

        // When
        List<CommandResult> results = commandExecutor.executeCommands(commands);

        // Then
        assertEquals(1, results.size());
        assertFalse(results.get(0).isSuccess());
        assertTrue(results.get(0).getErrorMessage().contains("Invalid command:"));
    }
}