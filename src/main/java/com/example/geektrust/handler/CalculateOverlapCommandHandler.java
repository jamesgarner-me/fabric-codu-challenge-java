package com.example.geektrust.handler;

import com.example.geektrust.command.ParsedCommand;
import com.example.geektrust.domain.Fund;
import com.example.geektrust.domain.Portfolio;
import com.example.geektrust.repository.FundRepository;
import com.example.geektrust.service.OverlapCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CalculateOverlapCommandHandler implements CommandHandler {
    private final Portfolio portfolio;
    private final FundRepository fundRepository;
    private final OverlapCalculator overlapCalculator;

    public CalculateOverlapCommandHandler(Portfolio portfolio, FundRepository fundRepository, OverlapCalculator overlapCalculator) {
        this.portfolio = portfolio;
        this.fundRepository = fundRepository;
        this.overlapCalculator = overlapCalculator;
    }

    @Override
    public CommandResult handle(ParsedCommand command) {
        String fundName = command.getArgument(0);
        Optional<Fund> targetFund = fundRepository.getFundByName(fundName);
        
        if (!targetFund.isPresent()) {
            return CommandResult.error("FUND_NOT_FOUND");
        }

        List<Fund> currentFunds = portfolio.getCurrentFunds();
        if (currentFunds.isEmpty()) {
            return CommandResult.success();
        }

        List<String> outputs = new ArrayList<>();
        for (Fund portfolioFund : currentFunds) {
            double overlap = overlapCalculator.calculateOverlapPercentage(targetFund.get(), portfolioFund);
            if (overlap > 0) {
                String output = String.format("%s %s %.2f%%", fundName, portfolioFund.getName(), overlap);
                outputs.add(output);
            }
        }

        return CommandResult.success(outputs);
    }
}