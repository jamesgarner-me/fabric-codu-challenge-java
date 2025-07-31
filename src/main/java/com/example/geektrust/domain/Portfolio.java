package com.example.geektrust.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Portfolio {
    private final List<Fund> currentFunds;

    public Portfolio() {
        this.currentFunds = new ArrayList<>();
    }

    public synchronized void setCurrentFunds(List<Fund> funds) {
        Objects.requireNonNull(funds, "Funds list cannot be null");
        
        this.currentFunds.clear();
        this.currentFunds.addAll(funds);
    }

    public synchronized List<Fund> getCurrentFunds() {
        return Collections.unmodifiableList(new ArrayList<>(currentFunds));
    }

    public synchronized boolean isEmpty() {
        return currentFunds.isEmpty();
    }

    public synchronized int size() {
        return currentFunds.size();
    }

    public synchronized void clear() {
        currentFunds.clear();
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(currentFunds, portfolio.currentFunds);
    }

    @Override
    public synchronized int hashCode() {
        return Objects.hash(currentFunds);
    }

    @Override
    public synchronized String toString() {
        return "Portfolio{" +
                "currentFunds=" + currentFunds +
                '}';
    }
}