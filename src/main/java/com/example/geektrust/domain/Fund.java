package com.example.geektrust.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Fund {
    private final String name;
    private final Set<String> stocks;

    public Fund(String name, Set<String> stocks) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Fund name cannot be null or empty");
        }
        if (stocks == null) {
            throw new IllegalArgumentException("Stocks cannot be null");
        }
        
        this.name = name.trim();
        this.stocks = Collections.unmodifiableSet(new HashSet<>(stocks));
    }

    public String getName() {
        return name;
    }

    public Set<String> getStocks() {
        return stocks;
    }

    public boolean containsStock(String stockName) {
        return stocks.contains(stockName);
    }

    public int getStockCount() {
        return stocks.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Fund fund = (Fund) o;
        return Objects.equals(name, fund.name) && 
               Objects.equals(stocks, fund.stocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, stocks);
    }

    @Override
    public String toString() {
        return "Fund{" +
                "name='" + name + '\'' +
                ", stocks=" + stocks +
                '}';
    }

    public static class Builder {
        private String name;
        private Set<String> stocks = new HashSet<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder addStock(String stock) {
            if (stock != null && !stock.trim().isEmpty()) {
                this.stocks.add(stock.trim());
            }
            return this;
        }

        public Builder addStocks(Set<String> stocks) {
            if (stocks != null) {
                stocks.stream()
                      .filter(stock -> stock != null && !stock.trim().isEmpty())
                      .forEach(stock -> this.stocks.add(stock.trim()));
            }
            return this;
        }

        public Fund build() {
            return new Fund(name, stocks);
        }
    }
}