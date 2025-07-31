package com.example.geektrust.repository;

import com.example.geektrust.domain.Fund;

import java.util.List;
import java.util.Optional;

public interface FundRepository {
    
    Optional<Fund> getFundByName(String fundName);
    
    List<Fund> getAllFunds();
}