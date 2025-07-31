package com.example.geektrust.handler;

import com.example.geektrust.command.ParsedCommand;
import com.example.geektrust.domain.Fund;
import com.example.geektrust.domain.Portfolio;
import com.example.geektrust.repository.FundRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrentPortfolioCommandHandler implements CommandHandler {
    private final Portfolio portfolio;
    private final FundRepository fundRepository;

    public CurrentPortfolioCommandHandler(Portfolio portfolio, FundRepository fundRepository) {
        this.portfolio = portfolio;
        this.fundRepository = fundRepository;
    }

    @Override
    public CommandResult handle(ParsedCommand command) {
        List<String> fundNames = command.getArguments();
        List<Fund> funds = new ArrayList<>();

        for (String fundName : fundNames) {
            Optional<Fund> fund = fundRepository.getFundByName(fundName);
            if (!fund.isPresent()) {
                return CommandResult.error("FUND_NOT_FOUND");
            }
            funds.add(fund.get());
        }

        portfolio.setCurrentFunds(funds);
        return CommandResult.success();
    }
}