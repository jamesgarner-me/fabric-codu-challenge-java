package com.example.geektrust.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandResult {
    private final boolean success;
    private final List<String> outputs;
    private final String errorMessage;

    private CommandResult(boolean success, List<String> outputs, String errorMessage) {
        this.success = success;
        this.outputs = Collections.unmodifiableList(new ArrayList<>(outputs != null ? outputs : new ArrayList<>()));
        this.errorMessage = errorMessage;
    }

    public static CommandResult success() {
        return new CommandResult(true, new ArrayList<>(), null);
    }

    public static CommandResult success(List<String> outputs) {
        return new CommandResult(true, outputs, null);
    }

    public static CommandResult error(String errorMessage) {
        return new CommandResult(false, new ArrayList<>(), errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasOutput() {
        return !outputs.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandResult that = (CommandResult) o;
        return success == that.success &&
               Objects.equals(outputs, that.outputs) &&
               Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, outputs, errorMessage);
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "success=" + success +
                ", outputs=" + outputs +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}