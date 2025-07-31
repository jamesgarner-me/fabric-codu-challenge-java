package com.example.geektrust.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Portfolio {
    private final List<String> currentFundNames;

    public Portfolio() {
        this.currentFundNames = new ArrayList<>();
    }

    public synchronized void setCurrentFundNames(List<String> fundNames) {
        Objects.requireNonNull(fundNames, "Fund names list cannot be null");
        
        this.currentFundNames.clear();
        this.currentFundNames.addAll(fundNames);
    }

    public synchronized List<String> getCurrentFundNames() {
        return Collections.unmodifiableList(new ArrayList<>(currentFundNames));
    }

    public synchronized boolean isEmpty() {
        return currentFundNames.isEmpty();
    }

    public synchronized int size() {
        return currentFundNames.size();
    }

    public synchronized void clear() {
        currentFundNames.clear();
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(currentFundNames, portfolio.currentFundNames);
    }

    @Override
    public synchronized int hashCode() {
        return Objects.hash(currentFundNames);
    }

    @Override
    public synchronized String toString() {
        return "Portfolio{" +
                "currentFundNames=" + currentFundNames +
                '}';
    }
}