package com.example.geektrust.handler;

import com.example.geektrust.command.CommandType;
import com.example.geektrust.command.ParsedCommand;
import com.example.geektrust.constants.ErrorMessages;
import com.example.geektrust.domain.Fund;
import com.example.geektrust.repository.JsonFundRepository;
import com.example.geektrust.repository.ModifiableFundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AddStockCommandHandlerTest {

    private AddStockCommandHandler handler;
    private ModifiableFundRepository fundRepository;

    @BeforeEach
    void setUp() {
        String stockDataPath = getClass().getClassLoader().getResource("stock_data.json").getPath();
        JsonFundRepository jsonRepository = new JsonFundRepository(stockDataPath);
        fundRepository = new ModifiableFundRepository(jsonRepository);
        handler = new AddStockCommandHandler(fundRepository);
    }

    @Test
    @DisplayName("Should add stock to existing fund")
    void shouldAddStockToExistingFund() {
        // Given
        ParsedCommand command = ParsedCommand.create(CommandType.ADD_STOCK, 
            Arrays.asList("AXIS_BLUECHIP", "NEW_STOCK"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertTrue(result.isSuccess());
        assertFalse(result.hasOutput());
        
        // Verify stock was added by checking fund has more stocks
        Fund fundBefore = new JsonFundRepository(getClass().getClassLoader().getResource("stock_data.json").getPath())
            .getFundByName("AXIS_BLUECHIP").get();
        Fund fundAfter = fundRepository.getFundByName("AXIS_BLUECHIP").get();
        assertTrue(fundAfter.getStockCount() > fundBefore.getStockCount());
    }

    @Test
    @DisplayName("Should return error when fund does not exist")
    void shouldReturnErrorWhenFundDoesNotExist() {
        // Given
        ParsedCommand command = ParsedCommand.create(CommandType.ADD_STOCK, 
            Arrays.asList("NON_EXISTENT_FUND", "SOME_STOCK"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(ErrorMessages.FUND_NOT_FOUND, result.getErrorMessage());
    }

    @Test
    @DisplayName("Should handle stock with spaces in name")
    void shouldHandleStockWithSpacesInName() {
        // Given
        ParsedCommand command = ParsedCommand.create(CommandType.ADD_STOCK, 
            Arrays.asList("AXIS_BLUECHIP", "STOCK WITH SPACES"));

        // When
        CommandResult result = handler.handle(command);

        // Then
        assertTrue(result.isSuccess());
    }
}