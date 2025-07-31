package com.example.geektrust.command;

import com.example.geektrust.exception.InvalidCommandException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParser {
    
    private static final int CURRENT_PORTFOLIO_MIN_ARGS = 1;
    private static final int CALCULATE_OVERLAP_MIN_ARGS = 1;
    private static final int ADD_STOCK_MIN_ARGS = 2;
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final int COMMAND_AND_ARGS_LIMIT = 2;
    
    public ParsedCommand parse(String commandLine) throws InvalidCommandException {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            throw new InvalidCommandException("Command line cannot be null or empty");
        }
        
        String trimmedLine = commandLine.trim();
        String[] parts = trimmedLine.split(WHITESPACE_REGEX, COMMAND_AND_ARGS_LIMIT);
        
        if (parts.length == 0) {
            throw new InvalidCommandException("Invalid command format");
        }
        
        String commandString = parts[0];
        CommandType commandType = parseCommandType(commandString);
        
        List<String> arguments = new ArrayList<>();
        if (parts.length > 1) {
            arguments = parseArguments(commandType, parts[1]);
        }
        
        validateArgumentCount(commandType, arguments.size());
        
        return ParsedCommand.create(commandType, arguments);
    }
    
    private CommandType parseCommandType(String commandString) throws InvalidCommandException {
        try {
            return CommandType.valueOf(commandString);
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException("Unknown command: " + commandString);
        }
    }
    
    private List<String> parseArguments(CommandType commandType, String argumentString) {
        List<String> arguments = new ArrayList<>();
        String trimmedArgs = argumentString.trim();
        
        if (trimmedArgs.isEmpty()) {
            return arguments;
        }
        
        switch (commandType) {
            case CURRENT_PORTFOLIO:
                // Multiple fund names separated by spaces
                arguments.addAll(Arrays.asList(trimmedArgs.split(WHITESPACE_REGEX)));
                break;
                
            case CALCULATE_OVERLAP:
                // Single fund name
                arguments.add(trimmedArgs);
                break;
                
            case ADD_STOCK:
                // Fund name followed by stock name (which can contain spaces)
                String[] addStockParts = trimmedArgs.split(WHITESPACE_REGEX, COMMAND_AND_ARGS_LIMIT);
                arguments.addAll(Arrays.asList(addStockParts));
                break;
                
            default:
                throw new IllegalStateException("Unhandled command type: " + commandType);
        }
        
        return arguments;
    }
    
    private void validateArgumentCount(CommandType commandType, int argCount) throws InvalidCommandException {
        switch (commandType) {
            case CURRENT_PORTFOLIO:
                if (argCount < CURRENT_PORTFOLIO_MIN_ARGS) {
                    throw new InvalidCommandException(
                        "CURRENT_PORTFOLIO requires at least " + CURRENT_PORTFOLIO_MIN_ARGS + " fund name(s)");
                }
                break;
                
            case CALCULATE_OVERLAP:
                if (argCount < CALCULATE_OVERLAP_MIN_ARGS) {
                    throw new InvalidCommandException(
                        "CALCULATE_OVERLAP requires exactly " + CALCULATE_OVERLAP_MIN_ARGS + " fund name");
                }
                break;
                
            case ADD_STOCK:
                if (argCount < ADD_STOCK_MIN_ARGS) {
                    throw new InvalidCommandException(
                        "ADD_STOCK requires " + ADD_STOCK_MIN_ARGS + " arguments: fund name and stock name");
                }
                break;
                
            default:
                throw new IllegalStateException("Unhandled command type: " + commandType);
        }
    }
}