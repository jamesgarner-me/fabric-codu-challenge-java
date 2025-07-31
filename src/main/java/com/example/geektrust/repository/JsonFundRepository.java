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
    private final Map<String, Fund> fundCache = new ConcurrentHashMap<>();
    private final List<Fund> allFunds = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean isLoaded = false;

    public JsonFundRepository(String jsonFilePath) {
        if (jsonFilePath == null || jsonFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON file path cannot be null or empty");
        }
        loadFunds(jsonFilePath);
    }

    public JsonFundRepository(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }
        loadFunds(inputStream);
    }

    private void loadFunds(String jsonFilePath) {
        try {
            File jsonFile = new File(jsonFilePath);
            if (!jsonFile.exists()) {
                LOGGER.log(Level.SEVERE, "JSON file not found: " + jsonFilePath);
                return;
            }
            processJson(objectMapper.readTree(jsonFile));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading funds from JSON file: " + jsonFilePath, e);
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
        if (rootNode == null || !rootNode.has("funds")) {
            LOGGER.log(Level.WARNING, "Invalid JSON structure: missing 'funds' array");
            return;
        }

        JsonNode fundsArray = rootNode.get("funds");
        if (!fundsArray.isArray()) {
            LOGGER.log(Level.WARNING, "Invalid JSON structure: 'funds' is not an array");
            return;
        }

        for (JsonNode fundNode : fundsArray) {
            try {
                String fundName = fundNode.get("name").asText();
                Set<String> stocks = new HashSet<>();
                
                JsonNode stocksArray = fundNode.get("stocks");
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
        LOGGER.log(Level.INFO, "Successfully loaded " + allFunds.size() + " funds");
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