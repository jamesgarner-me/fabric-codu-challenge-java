package com.example.geektrust.service;

import com.example.geektrust.command.CommandParser;
import com.example.geektrust.command.CommandType;
import com.example.geektrust.command.ParsedCommand;
import com.example.geektrust.exception.InvalidCommandException;
import com.example.geektrust.handler.CommandHandler;
import com.example.geektrust.handler.CommandResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandExecutor {
    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class.getName());
    
    private final Map<CommandType, CommandHandler> handlers = new HashMap<>();
    private final CommandParser commandParser = new CommandParser();

    public void registerHandler(CommandType commandType, CommandHandler handler) {
        handlers.put(commandType, handler);
    }

    public List<CommandResult> executeCommands(List<String> commandLines) {
        List<CommandResult> results = new ArrayList<>();
        
        for (String commandLine : commandLines) {
            try {
                ParsedCommand command = commandParser.parse(commandLine);
                CommandHandler handler = handlers.get(command.getCommandType());
                
                if (handler == null) {
                    results.add(CommandResult.error("No handler found for command: " + command.getCommandType()));
                    continue;
                }
                
                CommandResult result = handler.handle(command);
                results.add(result);
                
            } catch (InvalidCommandException e) {
                LOGGER.log(Level.WARNING, "Invalid command: " + commandLine, e);
                results.add(CommandResult.error("Invalid command: " + e.getMessage()));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error executing command: " + commandLine, e);
                results.add(CommandResult.error("Error executing command: " + e.getMessage()));
            }
        }
        
        return results;
    }
}