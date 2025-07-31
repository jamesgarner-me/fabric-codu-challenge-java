package com.example.geektrust.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParsedCommandTest {

    @Test
    @DisplayName("Should create ParsedCommand with valid data")
    void shouldCreateParsedCommandWithValidData() {
        List<String> arguments = Arrays.asList("ARG1", "ARG2");
        ParsedCommand command = ParsedCommand.create(CommandType.CURRENT_PORTFOLIO, arguments);
        
        assertEquals(CommandType.CURRENT_PORTFOLIO, command.getCommandType());
        assertEquals(2, command.getArgumentCount());
        assertEquals("ARG1", command.getArgument(0));
        assertEquals("ARG2", command.getArgument(1));
    }

    @Test
    @DisplayName("Should throw exception for null command type")
    void shouldThrowExceptionForNullCommandType() {
        assertThrows(NullPointerException.class, () -> {
            ParsedCommand.create(null, Arrays.asList("ARG1"));
        });
    }

    @Test
    @DisplayName("Should throw exception for null arguments")
    void shouldThrowExceptionForNullArguments() {
        assertThrows(NullPointerException.class, () -> {
            ParsedCommand.create(CommandType.CURRENT_PORTFOLIO, null);
        });
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        List<String> args1 = Arrays.asList("ARG1", "ARG2");
        List<String> args2 = Arrays.asList("ARG1", "ARG2");
        List<String> args3 = Arrays.asList("ARG1", "ARG3");
        
        ParsedCommand command1 = ParsedCommand.create(CommandType.CURRENT_PORTFOLIO, args1);
        ParsedCommand command2 = ParsedCommand.create(CommandType.CURRENT_PORTFOLIO, args2);
        ParsedCommand command3 = ParsedCommand.create(CommandType.CALCULATE_OVERLAP, args1);
        ParsedCommand command4 = ParsedCommand.create(CommandType.CURRENT_PORTFOLIO, args3);
        
        // Same instance
        assertEquals(command1, command1);
        
        // Equal commands
        assertEquals(command1, command2);
        assertEquals(command1.hashCode(), command2.hashCode());
        
        // Different command type
        assertNotEquals(command1, command3);
        
        // Different arguments
        assertNotEquals(command1, command4);
        
        // Null and different type
        assertNotEquals(command1, null);
        assertNotEquals(command1, "STRING");
    }

    @Test
    @DisplayName("Should implement toString")
    void shouldImplementToString() {
        ParsedCommand command = ParsedCommand.create(
            CommandType.ADD_STOCK, 
            Arrays.asList("FUND1", "STOCK1")
        );
        
        String toString = command.toString();
        assertTrue(toString.contains("ADD_STOCK"));
        assertTrue(toString.contains("FUND1"));
        assertTrue(toString.contains("STOCK1"));
    }
}