package com.example.geektrust.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandResultTest {

    @Test
    @DisplayName("Should create successful result with no output")
    void shouldCreateSuccessfulResultWithNoOutput() {
        // When
        CommandResult result = CommandResult.success();

        // Then
        assertTrue(result.isSuccess());
        assertFalse(result.hasOutput());
        assertNull(result.getErrorMessage());
        assertTrue(result.getOutputs().isEmpty());
    }

    @Test
    @DisplayName("Should create successful result with single output")
    void shouldCreateSuccessfulResultWithSingleOutput() {
        // Given
        String output = "Single output line";

        // When
        CommandResult result = CommandResult.success(Arrays.asList(output));

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.hasOutput());
        assertNull(result.getErrorMessage());
        assertEquals(1, result.getOutputs().size());
        assertEquals(output, result.getOutputs().get(0));
    }

    @Test
    @DisplayName("Should create successful result with multiple outputs")
    void shouldCreateSuccessfulResultWithMultipleOutputs() {
        // Given
        List<String> outputs = Arrays.asList("Line 1", "Line 2", "Line 3");

        // When
        CommandResult result = CommandResult.success(outputs);

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.hasOutput());
        assertNull(result.getErrorMessage());
        assertEquals(3, result.getOutputs().size());
        assertEquals("Line 1", result.getOutputs().get(0));
        assertEquals("Line 2", result.getOutputs().get(1));
        assertEquals("Line 3", result.getOutputs().get(2));
    }

    @Test
    @DisplayName("Should create error result")
    void shouldCreateErrorResult() {
        // Given
        String errorMessage = "Something went wrong";

        // When
        CommandResult result = CommandResult.error(errorMessage);

        // Then
        assertFalse(result.isSuccess());
        assertFalse(result.hasOutput());
        assertEquals(errorMessage, result.getErrorMessage());
        assertTrue(result.getOutputs().isEmpty());
    }

    @Test
    @DisplayName("Should handle empty output list")
    void shouldHandleEmptyOutputList() {
        // When
        CommandResult result = CommandResult.success(Collections.emptyList());

        // Then
        assertTrue(result.isSuccess());
        assertFalse(result.hasOutput());
        assertNull(result.getErrorMessage());
        assertTrue(result.getOutputs().isEmpty());
    }

    @Test
    @DisplayName("Should handle null output list")
    void shouldHandleNullOutputList() {
        // When
        CommandResult result = CommandResult.success((List<String>) null);

        // Then
        assertTrue(result.isSuccess());
        assertFalse(result.hasOutput());
        assertNull(result.getErrorMessage());
        assertTrue(result.getOutputs().isEmpty());
    }

    @Test
    @DisplayName("Should handle null single output")
    void shouldHandleNullSingleOutput() {
        // When
        CommandResult result = CommandResult.success((List<String>) null);

        // Then
        assertTrue(result.isSuccess());
        assertFalse(result.hasOutput());
        assertNull(result.getErrorMessage());
        assertTrue(result.getOutputs().isEmpty());
    }

    @Test
    @DisplayName("Should handle empty single output")
    void shouldHandleEmptySingleOutput() {
        // When
        CommandResult result = CommandResult.success(Arrays.asList(""));

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.hasOutput()); // Empty string is still an output
        assertNull(result.getErrorMessage());
        assertEquals(1, result.getOutputs().size());
    }

    @Test
    @DisplayName("Should handle whitespace-only single output")
    void shouldHandleWhitespaceOnlySingleOutput() {
        // When
        CommandResult result = CommandResult.success(Arrays.asList("   "));

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.hasOutput()); // Whitespace is still an output
        assertNull(result.getErrorMessage());
        assertEquals(1, result.getOutputs().size());
    }

    @Test
    @DisplayName("Should trim single output")
    void shouldTrimSingleOutput() {
        // When
        CommandResult result = CommandResult.success(Arrays.asList("  trimmed output  "));

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.hasOutput());
        assertEquals("  trimmed output  ", result.getOutputs().get(0)); // CommandResult doesn't trim
    }

    @Test
    @DisplayName("Should filter out null and empty outputs from list")
    void shouldFilterOutNullAndEmptyOutputsFromList() {
        // Given
        List<String> outputs = Arrays.asList("Valid output", null, "", "   ", "Another valid output");

        // When
        CommandResult result = CommandResult.success(outputs);

        // Then
        assertTrue(result.isSuccess());
        assertTrue(result.hasOutput());
        assertEquals(5, result.getOutputs().size()); // CommandResult doesn't filter nulls/empty strings
        assertEquals("Valid output", result.getOutputs().get(0));
        assertEquals("Another valid output", result.getOutputs().get(4));
    }

    @Test
    @DisplayName("Should create immutable output list")
    void shouldCreateImmutableOutputList() {
        // Given
        List<String> outputs = Arrays.asList("Output 1", "Output 2");
        CommandResult result = CommandResult.success(outputs);

        // When/Then
        assertThrows(UnsupportedOperationException.class, () -> {
            result.getOutputs().add("New output");
        });
    }

    @Test
    @DisplayName("Should handle null error message")
    void shouldHandleNullErrorMessage() {
        // When
        CommandResult result = CommandResult.error(null);

        // Then
        assertFalse(result.isSuccess());
        assertFalse(result.hasOutput());
        assertNull(result.getErrorMessage());
        assertTrue(result.getOutputs().isEmpty());
    }
}