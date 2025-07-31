package com.example.geektrust.repository;

import com.example.geektrust.domain.Fund;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonFundRepositoryTest {

    @TempDir
    Path tempDir;

    private String validJson;
    private String invalidJson;
    private String emptyJson;

    @BeforeEach
    void setUp() {
        validJson = "{\n" +
                "  \"funds\": [\n" +
                "    {\n" +
                "      \"name\": \"FUND1\",\n" +
                "      \"stocks\": [\"STOCK1\", \"STOCK2\", \"STOCK3\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"FUND2\",\n" +
                "      \"stocks\": [\"STOCK2\", \"STOCK4\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"EMPTY_FUND\",\n" +
                "      \"stocks\": []\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        invalidJson = "{\n" +
                "  \"invalid\": \"structure\"\n" +
                "}";

        emptyJson = "{\n" +
                "  \"funds\": []\n" +
                "}";
    }

    @Test
    @DisplayName("Should load funds from valid JSON file")
    void shouldLoadFundsFromValidJsonFile() throws IOException {
        File jsonFile = createTempJsonFile("valid.json", validJson);
        
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        assertTrue(repository.isLoaded());
        assertEquals(3, repository.getAllFunds().size());
    }

    @Test
    @DisplayName("Should load funds from input stream")
    void shouldLoadFundsFromInputStream() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(validJson.getBytes());
        
        JsonFundRepository repository = new JsonFundRepository(inputStream);
        
        assertTrue(repository.isLoaded());
        assertEquals(3, repository.getAllFunds().size());
    }

    @Test
    @DisplayName("Should retrieve fund by name")
    void shouldRetrieveFundByName() throws IOException {
        File jsonFile = createTempJsonFile("funds.json", validJson);
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        Optional<Fund> fund1 = repository.getFundByName("FUND1");
        Optional<Fund> fund2 = repository.getFundByName("FUND2");
        Optional<Fund> emptyFund = repository.getFundByName("EMPTY_FUND");
        
        assertTrue(fund1.isPresent());
        assertEquals("FUND1", fund1.get().getName());
        assertEquals(3, fund1.get().getStockCount());
        assertTrue(fund1.get().containsStock("STOCK1"));
        assertTrue(fund1.get().containsStock("STOCK2"));
        assertTrue(fund1.get().containsStock("STOCK3"));
        
        assertTrue(fund2.isPresent());
        assertEquals("FUND2", fund2.get().getName());
        assertEquals(2, fund2.get().getStockCount());
        
        assertTrue(emptyFund.isPresent());
        assertEquals(0, emptyFund.get().getStockCount());
    }

    @Test
    @DisplayName("Should return empty Optional for non-existent fund")
    void shouldReturnEmptyOptionalForNonExistentFund() throws IOException {
        File jsonFile = createTempJsonFile("funds.json", validJson);
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        Optional<Fund> nonExistent = repository.getFundByName("NON_EXISTENT");
        
        assertFalse(nonExistent.isPresent());
    }

    @Test
    @DisplayName("Should return empty Optional for null fund name")
    void shouldReturnEmptyOptionalForNullFundName() throws IOException {
        File jsonFile = createTempJsonFile("funds.json", validJson);
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        Optional<Fund> nullFund = repository.getFundByName(null);
        
        assertFalse(nullFund.isPresent());
    }

    @Test
    @DisplayName("Should handle case sensitivity in fund names")
    void shouldHandleCaseSensitivityInFundNames() throws IOException {
        File jsonFile = createTempJsonFile("funds.json", validJson);
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        Optional<Fund> correctCase = repository.getFundByName("FUND1");
        Optional<Fund> wrongCase = repository.getFundByName("fund1");
        
        assertTrue(correctCase.isPresent());
        assertFalse(wrongCase.isPresent());
    }

    @Test
    @DisplayName("Should return unmodifiable list of all funds")
    void shouldReturnUnmodifiableListOfAllFunds() throws IOException {
        File jsonFile = createTempJsonFile("funds.json", validJson);
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        List<Fund> allFunds = repository.getAllFunds();
        
        assertEquals(3, allFunds.size());
        assertThrows(UnsupportedOperationException.class, () -> {
            allFunds.add(new Fund("NEW_FUND", new java.util.HashSet<>()));
        });
    }

    @Test
    @DisplayName("Should handle invalid JSON structure gracefully")
    void shouldHandleInvalidJsonStructureGracefully() throws IOException {
        File jsonFile = createTempJsonFile("invalid.json", invalidJson);
        
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        assertTrue(repository.getAllFunds().isEmpty());
    }

    @Test
    @DisplayName("Should handle empty funds array")
    void shouldHandleEmptyFundsArray() throws IOException {
        File jsonFile = createTempJsonFile("empty.json", emptyJson);
        
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        assertTrue(repository.isLoaded());
        assertTrue(repository.getAllFunds().isEmpty());
    }

    @Test
    @DisplayName("Should handle non-existent file")
    void shouldHandleNonExistentFile() {
        JsonFundRepository repository = new JsonFundRepository("/non/existent/file.json");
        
        assertFalse(repository.isLoaded());
        assertTrue(repository.getAllFunds().isEmpty());
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void shouldHandleMalformedJson() throws IOException {
        File jsonFile = createTempJsonFile("malformed.json", "{ invalid json }");
        
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        assertFalse(repository.isLoaded());
        assertTrue(repository.getAllFunds().isEmpty());
    }

    @Test
    @DisplayName("Should handle funds with missing fields gracefully")
    void shouldHandleFundsWithMissingFieldsGracefully() throws IOException {
        String jsonWithMissingFields = "{\n" +
                "  \"funds\": [\n" +
                "    {\n" +
                "      \"name\": \"VALID_FUND\",\n" +
                "      \"stocks\": [\"STOCK1\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"stocks\": [\"STOCK2\"]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"FUND_NO_STOCKS\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        
        File jsonFile = createTempJsonFile("missing_fields.json", jsonWithMissingFields);
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        assertEquals(2, repository.getAllFunds().size());
        assertTrue(repository.getFundByName("VALID_FUND").isPresent());
        assertTrue(repository.getFundByName("FUND_NO_STOCKS").isPresent());
    }

    @Test
    @DisplayName("Should throw exception for null file path")
    void shouldThrowExceptionForNullFilePath() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonFundRepository((String) null);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty file path")
    void shouldThrowExceptionForEmptyFilePath() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonFundRepository("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonFundRepository("   ");
        });
    }

    @Test
    @DisplayName("Should throw exception for null input stream")
    void shouldThrowExceptionForNullInputStream() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonFundRepository((ByteArrayInputStream) null);
        });
    }

    @Test
    @DisplayName("Should preserve fund order from JSON")
    void shouldPreserveFundOrderFromJson() throws IOException {
        File jsonFile = createTempJsonFile("ordered.json", validJson);
        JsonFundRepository repository = new JsonFundRepository(jsonFile.getAbsolutePath());
        
        List<Fund> allFunds = repository.getAllFunds();
        
        assertEquals("FUND1", allFunds.get(0).getName());
        assertEquals("FUND2", allFunds.get(1).getName());
        assertEquals("EMPTY_FUND", allFunds.get(2).getName());
    }

    private File createTempJsonFile(String fileName, String content) throws IOException {
        File file = new File(tempDir.toFile(), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }
}