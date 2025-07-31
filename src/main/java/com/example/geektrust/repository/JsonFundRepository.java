package com.example.geektrust.repository;

import com.example.geektrust.domain.Fund;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

public class JsonFundRepository implements FundRepository {
    private static final Logger LOGGER = Logger.getLogger(JsonFundRepository.class.getName());
    private static final String FUNDS_ARRAY_KEY = "funds";
    private static final String FUND_NAME_KEY = "name";
    private static final String FUND_STOCKS_KEY = "stocks";
    
    private final Map<String, Fund> fundCache = new ConcurrentHashMap<>();
    private final List<Fund> allFunds = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean isLoaded = false;

    public JsonFundRepository(String jsonFilePath) {
        if (jsonFilePath == null || jsonFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON file path cannot be null or empty");
        }
        loadFundsFromPath(jsonFilePath);
    }

    public JsonFundRepository(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }
        loadFunds(inputStream);
    }

    private void loadFundsFromPath(String jsonFilePath) {
        // Try multiple strategies to load the file
        InputStream inputStream = null;
        
        try {
            // Strategy 1: Try as file (handles both relative and absolute paths)
            File jsonFile = new File(jsonFilePath);
            if (jsonFile.exists()) {
                // LOGGER.log(Level.INFO, "Loading funds from file: " + jsonFile.getAbsolutePath());
                processJson(objectMapper.readTree(jsonFile));
                return;
            }
            
            // Strategy 3: Try as classpath resource (fallback for packaged JAR)
            inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath);
            if (inputStream != null) {
                // LOGGER.log(Level.INFO, "Loading funds from classpath resource: " + jsonFilePath);
                processJson(objectMapper.readTree(inputStream));
                return;
            }
            
            LOGGER.log(Level.SEVERE, "JSON file not found in any location: " + jsonFilePath);
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading funds from JSON: " + jsonFilePath, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error closing input stream", e);
                }
            }
        }
    }

    private void loadFunds(InputStream inputStream) {
        try {
            processJson(objectMapper.readTree(inputStream));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading funds from input stream", e);
        }
    }

    private void processJson(JsonNode rootNode) {
        if (rootNode == null || !rootNode.has(FUNDS_ARRAY_KEY)) {
            LOGGER.log(Level.WARNING, "Invalid JSON structure: missing '" + FUNDS_ARRAY_KEY + "' array");
            return;
        }

        JsonNode fundsArray = rootNode.get(FUNDS_ARRAY_KEY);
        if (!fundsArray.isArray()) {
            LOGGER.log(Level.WARNING, "Invalid JSON structure: '" + FUNDS_ARRAY_KEY + "' is not an array");
            return;
        }

        for (JsonNode fundNode : fundsArray) {
            try {
                String fundName = fundNode.get(FUND_NAME_KEY).asText();
                Set<String> stocks = new HashSet<>();
                
                JsonNode stocksArray = fundNode.get(FUND_STOCKS_KEY);
                if (stocksArray != null && stocksArray.isArray()) {
                    for (JsonNode stockNode : stocksArray) {
                        stocks.add(stockNode.asText());
                    }
                }
                
                Fund fund = new Fund(fundName, stocks);
                fundCache.put(fundName, fund);
                allFunds.add(fund);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error parsing fund: " + fundNode, e);
            }
        }
        
        isLoaded = true;
        // LOGGER.log(Level.INFO, "Successfully loaded " + allFunds.size() + " funds");
    }

    @Override
    public Optional<Fund> getFundByName(String fundName) {
        if (fundName == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(fundCache.get(fundName));
    }

    @Override
    public List<Fund> getAllFunds() {
        return Collections.unmodifiableList(new ArrayList<>(allFunds));
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}