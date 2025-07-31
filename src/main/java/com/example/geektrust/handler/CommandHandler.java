package com.example.geektrust.handler;

import com.example.geektrust.command.ParsedCommand;

public interface CommandHandler {
    CommandResult handle(ParsedCommand command);
}