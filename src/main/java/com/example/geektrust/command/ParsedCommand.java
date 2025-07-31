package com.example.geektrust.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ParsedCommand {
    private final CommandType commandType;
    private final List<String> arguments;

    private ParsedCommand(CommandType commandType, List<String> arguments) {
        this.commandType = Objects.requireNonNull(commandType, "Command type cannot be null");
        this.arguments = Collections.unmodifiableList(new ArrayList<>(
                Objects.requireNonNull(arguments, "Arguments cannot be null")));
    }

    public static ParsedCommand create(CommandType commandType, List<String> arguments) {
        return new ParsedCommand(commandType, arguments);
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public int getArgumentCount() {
        return arguments.size();
    }

    public String getArgument(int index) {
        if (index < 0 || index >= arguments.size()) {
            throw new IndexOutOfBoundsException("Argument index out of bounds: " + index);
        }
        return arguments.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedCommand that = (ParsedCommand) o;
        return commandType == that.commandType && 
               Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandType, arguments);
    }

    @Override
    public String toString() {
        return "ParsedCommand{" +
                "commandType=" + commandType +
                ", arguments=" + arguments +
                '}';
    }
}