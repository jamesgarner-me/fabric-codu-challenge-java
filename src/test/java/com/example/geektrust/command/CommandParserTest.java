package com.example.geektrust.command;

import com.example.geektrust.exception.InvalidCommandException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    @Test
    @DisplayName("Should parse CURRENT_PORTFOLIO with single fund")
    void shouldParseCurrentPortfolioWithSingleFund() throws InvalidCommandException {
        ParsedCommand command = parser.parse("CURRENT_PORTFOLIO FUND1");
        
        assertEquals(CommandType.CURRENT_PORTFOLIO, command.getCommandType());
        assertEquals(1, command.getArgumentCount());
        assertEquals("FUND1", command.getArgument(0));
    }

    @Test
    @DisplayName("Should parse CURRENT_PORTFOLIO with multiple funds")
    void shouldParseCurrentPortfolioWithMultipleFunds() throws InvalidCommandException {
        ParsedCommand command = parser.parse("CURRENT_PORTFOLIO FUND1 FUND2 FUND3");
        
        assertEquals(CommandType.CURRENT_PORTFOLIO, command.getCommandType());
        assertEquals(3, command.getArgumentCount());
        assertEquals("FUND1", command.getArgument(0));
        assertEquals("FUND2", command.getArgument(1));
        assertEquals("FUND3", command.getArgument(2));
    }

    @Test
    @DisplayName("Should parse CALCULATE_OVERLAP with fund name")
    void shouldParseCalculateOverlapWithFundName() throws InvalidCommandException {
        ParsedCommand command = parser.parse("CALCULATE_OVERLAP MIRAE_ASSET_EMERGING_BLUECHIP");
        
        assertEquals(CommandType.CALCULATE_OVERLAP, command.getCommandType());
        assertEquals(1, command.getArgumentCount());
        assertEquals("MIRAE_ASSET_EMERGING_BLUECHIP", command.getArgument(0));
    }

    @Test
    @DisplayName("Should parse ADD_STOCK with simple stock name")
    void shouldParseAddStockWithSimpleStockName() throws InvalidCommandException {
        ParsedCommand command = parser.parse("ADD_STOCK AXIS_BLUECHIP TCS");
        
        assertEquals(CommandType.ADD_STOCK, command.getCommandType());
        assertEquals(2, command.getArgumentCount());
        assertEquals("AXIS_BLUECHIP", command.getArgument(0));
        assertEquals("TCS", command.getArgument(1));
    }

    @Test
    @DisplayName("Should parse ADD_STOCK with stock name containing spaces")
    void shouldParseAddStockWithStockNameContainingSpaces() throws InvalidCommandException {
        ParsedCommand command = parser.parse("ADD_STOCK AXIS_BLUECHIP HDFC BANK LIMITED");
        
        assertEquals(CommandType.ADD_STOCK, command.getCommandType());
        assertEquals(2, command.getArgumentCount());
        assertEquals("AXIS_BLUECHIP", command.getArgument(0));
        assertEquals("HDFC BANK LIMITED", command.getArgument(1));
    }

    @Test
    @DisplayName("Should handle extra whitespace in commands")
    void shouldHandleExtraWhitespaceInCommands() throws InvalidCommandException {
        ParsedCommand command1 = parser.parse("  CURRENT_PORTFOLIO   FUND1   FUND2  ");
        assertEquals(CommandType.CURRENT_PORTFOLIO, command1.getCommandType());
        assertEquals(2, command1.getArgumentCount());
        assertEquals("FUND1", command1.getArgument(0));
        assertEquals("FUND2", command1.getArgument(1));

        ParsedCommand command2 = parser.parse("   ADD_STOCK   FUND1   STOCK NAME WITH SPACES   ");
        assertEquals(CommandType.ADD_STOCK, command2.getCommandType());
        assertEquals(2, command2.getArgumentCount());
        assertEquals("FUND1", command2.getArgument(0));
        assertEquals("STOCK NAME WITH SPACES", command2.getArgument(1));
    }

    @Test
    @DisplayName("Should throw exception for null command line")
    void shouldThrowExceptionForNullCommandLine() {
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty command line")
    void shouldThrowExceptionForEmptyCommandLine() {
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse("");
        });
        
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse("   ");
        });
    }

    @Test
    @DisplayName("Should throw exception for invalid command type")
    void shouldThrowExceptionForInvalidCommandType() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () -> {
            parser.parse("INVALID_COMMAND ARG1");
        });
        assertTrue(exception.getMessage().contains("Unknown command"));
    }

    @Test
    @DisplayName("Should throw exception for CURRENT_PORTFOLIO without arguments")
    void shouldThrowExceptionForCurrentPortfolioWithoutArguments() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () -> {
            parser.parse("CURRENT_PORTFOLIO");
        });
        assertTrue(exception.getMessage().contains("CURRENT_PORTFOLIO requires"));
    }

    @Test
    @DisplayName("Should throw exception for CALCULATE_OVERLAP without arguments")
    void shouldThrowExceptionForCalculateOverlapWithoutArguments() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () -> {
            parser.parse("CALCULATE_OVERLAP");
        });
        assertTrue(exception.getMessage().contains("CALCULATE_OVERLAP requires"));
    }

    @Test
    @DisplayName("Should throw exception for ADD_STOCK without sufficient arguments")
    void shouldThrowExceptionForAddStockWithoutSufficientArguments() {
        InvalidCommandException exception1 = assertThrows(InvalidCommandException.class, () -> {
            parser.parse("ADD_STOCK");
        });
        assertTrue(exception1.getMessage().contains("ADD_STOCK requires"));

        InvalidCommandException exception2 = assertThrows(InvalidCommandException.class, () -> {
            parser.parse("ADD_STOCK FUND1");
        });
        assertTrue(exception2.getMessage().contains("ADD_STOCK requires"));
    }

    @Test
    @DisplayName("Should parse commands with tabs and mixed whitespace")
    void shouldParseCommandsWithTabsAndMixedWhitespace() throws InvalidCommandException {
        ParsedCommand command = parser.parse("CURRENT_PORTFOLIO\tFUND1\t\tFUND2   FUND3");
        
        assertEquals(CommandType.CURRENT_PORTFOLIO, command.getCommandType());
        assertEquals(3, command.getArgumentCount());
        assertEquals("FUND1", command.getArgument(0));
        assertEquals("FUND2", command.getArgument(1));
        assertEquals("FUND3", command.getArgument(2));
    }

    @Test
    @DisplayName("Should preserve internal spaces in stock names")
    void shouldPreserveInternalSpacesInStockNames() throws InvalidCommandException {
        ParsedCommand command = parser.parse("ADD_STOCK FUND1 STOCK   WITH   MULTIPLE   SPACES");
        
        assertEquals(CommandType.ADD_STOCK, command.getCommandType());
        assertEquals(2, command.getArgumentCount());
        assertEquals("FUND1", command.getArgument(0));
        assertEquals("STOCK   WITH   MULTIPLE   SPACES", command.getArgument(1));
    }

    @Test
    @DisplayName("Should handle case sensitivity in commands")
    void shouldHandleCaseSensitivityInCommands() {
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse("current_portfolio FUND1");
        });
        
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse("Calculate_Overlap FUND1");
        });
    }

    @Test
    @DisplayName("ParsedCommand should throw exception for invalid argument index")
    void parsedCommandShouldThrowExceptionForInvalidArgumentIndex() throws InvalidCommandException {
        ParsedCommand command = parser.parse("CURRENT_PORTFOLIO FUND1");
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            command.getArgument(-1);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            command.getArgument(1);
        });
    }

    @Test
    @DisplayName("ParsedCommand should be immutable")
    void parsedCommandShouldBeImmutable() throws InvalidCommandException {
        ParsedCommand command = parser.parse("CURRENT_PORTFOLIO FUND1 FUND2");
        List<String> arguments = command.getArguments();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            arguments.add("FUND3");
        });
    }
}