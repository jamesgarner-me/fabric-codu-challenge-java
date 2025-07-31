package com.example.geektrust;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    private Application application;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Create a test stock data file
        String stockDataPath = getClass().getClassLoader().getResource("stock_data.json").getPath();
        application = new Application(stockDataPath);
        
        // Capture system output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Should handle valid command sequence")
    void shouldHandleValidCommandSequence() throws IOException {
        // Given
        Path inputFile = tempDir.resolve("valid_input.txt");
        Files.write(inputFile, Arrays.asList(
            "CURRENT_PORTFOLIO AXIS_BLUECHIP ICICI_PRU_BLUECHIP",
            "CALCULATE_OVERLAP MIRAE_ASSET_EMERGING_BLUECHIP"
        ));

        // When
        application.run(inputFile.toString());

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("MIRAE_ASSET_EMERGING_BLUECHIP AXIS_BLUECHIP"));
        assertTrue(output.contains("MIRAE_ASSET_EMERGING_BLUECHIP ICICI_PRU_BLUECHIP"));
    }

    @Test
    @DisplayName("Should handle invalid fund name")
    void shouldHandleInvalidFundName() throws IOException {
        // Given
        Path inputFile = tempDir.resolve("invalid_fund_input.txt");
        Files.write(inputFile, Arrays.asList(
            "CURRENT_PORTFOLIO NON_EXISTENT_FUND"
        ));

        // When
        application.run(inputFile.toString());

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("FUND_NOT_FOUND"));
    }

    @Test
    @DisplayName("Should handle empty portfolio calculation")
    void shouldHandleEmptyPortfolioCalculation() throws IOException {
        // Given
        Path inputFile = tempDir.resolve("empty_portfolio_input.txt");
        Files.write(inputFile, Arrays.asList(
            "CALCULATE_OVERLAP MIRAE_ASSET_EMERGING_BLUECHIP"
        ));

        // When
        application.run(inputFile.toString());

        // Then
        String output = outputStream.toString();
        // Should have no output for empty portfolio
        assertEquals("", output.trim());
    }

    @Test
    @DisplayName("Should handle ADD_STOCK command")
    void shouldHandleAddStockCommand() throws IOException {
        // Given
        Path inputFile = tempDir.resolve("add_stock_input.txt");
        Files.write(inputFile, Arrays.asList(
            "CURRENT_PORTFOLIO AXIS_BLUECHIP",
            "CALCULATE_OVERLAP MIRAE_ASSET_EMERGING_BLUECHIP",
            "ADD_STOCK AXIS_BLUECHIP NEW_STOCK",
            "CALCULATE_OVERLAP MIRAE_ASSET_EMERGING_BLUECHIP"
        ));

        // When
        application.run(inputFile.toString());

        // Then
        String output = outputStream.toString();
        String[] lines = output.trim().split("\n");
        assertEquals(2, lines.length);
        // Both should contain the same fund comparison but potentially different percentages
        assertTrue(lines[0].contains("MIRAE_ASSET_EMERGING_BLUECHIP AXIS_BLUECHIP"));
        assertTrue(lines[1].contains("MIRAE_ASSET_EMERGING_BLUECHIP AXIS_BLUECHIP"));
    }

    @Test
    @DisplayName("Should handle invalid command")
    void shouldHandleInvalidCommand() throws IOException {
        // Given
        Path inputFile = tempDir.resolve("invalid_command_input.txt");
        Files.write(inputFile, Arrays.asList(
            "INVALID_COMMAND SOME_PARAM"
        ));

        // When
        application.run(inputFile.toString());

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid command:"));
    }

    @Test
    @DisplayName("Should handle empty input file")
    void shouldHandleEmptyInputFile() throws IOException {
        // Given
        Path inputFile = tempDir.resolve("empty_input.txt");
        Files.write(inputFile, Collections.emptyList());

        // When
        application.run(inputFile.toString());

        // Then
        String output = outputStream.toString();
        assertEquals("", output.trim());
    }

    @Test
    @DisplayName("Should handle malformed command")
    void shouldHandleMalformedCommand() throws IOException {
        // Given
        Path inputFile = tempDir.resolve("malformed_input.txt");
        Files.write(inputFile, Arrays.asList(
            "CURRENT_PORTFOLIO", // Missing required arguments
            "CALCULATE_OVERLAP", // Missing required arguments
            "ADD_STOCK FUND_ONLY" // Missing stock name
        ));

        // When
        application.run(inputFile.toString());

        // Then
        String output = outputStream.toString();
        String[] lines = output.trim().split("\n");
        assertEquals(3, lines.length);
        for (String line : lines) {
            assertTrue(line.contains("Invalid command:"));
        }
    }

    @Test
    @DisplayName("Should handle mixed valid and invalid commands")
    void shouldHandleMixedValidAndInvalidCommands() throws IOException {
        // Given
        Path inputFile = tempDir.resolve("mixed_input.txt");
        Files.write(inputFile, Arrays.asList(
            "CURRENT_PORTFOLIO AXIS_BLUECHIP",
            "INVALID_COMMAND",
            "CALCULATE_OVERLAP MIRAE_ASSET_EMERGING_BLUECHIP"
        ));

        // When
        application.run(inputFile.toString());

        // Then
        String output = outputStream.toString();
        String[] lines = output.split("\n");
        
        // Should have error message and valid output
        boolean hasErrorMessage = false;
        boolean hasValidOutput = false;
        
        for (String line : lines) {
            if (line.contains("Invalid command:")) {
                hasErrorMessage = true;
            }
            if (line.contains("MIRAE_ASSET_EMERGING_BLUECHIP AXIS_BLUECHIP")) {
                hasValidOutput = true;
            }
        }
        
        assertTrue(hasErrorMessage);
        assertTrue(hasValidOutput);
    }

    @Test
    @DisplayName("Should handle command list directly")
    void shouldHandleCommandListDirectly() {
        // Given
        java.util.List<String> commands = Arrays.asList(
            "CURRENT_PORTFOLIO AXIS_BLUECHIP",
            "CALCULATE_OVERLAP MIRAE_ASSET_EMERGING_BLUECHIP"
        );

        // When
        application.run(commands);

        // Then
        String output = outputStream.toString();
        assertTrue(output.contains("MIRAE_ASSET_EMERGING_BLUECHIP AXIS_BLUECHIP"));
    }

    @Test
    @DisplayName("Should handle non-existent input file")
    void shouldHandleNonExistentInputFile() {
        // When/Then
        assertThrows(IOException.class, () -> {
            application.run("/non/existent/file.txt");
        });
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}