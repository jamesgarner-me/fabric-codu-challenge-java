package com.example.geektrust.handler;

import com.example.geektrust.command.ParsedCommand;
import com.example.geektrust.constants.ErrorMessages;
import com.example.geektrust.repository.FundRepository;
import com.example.geektrust.repository.ModifiableFundRepository;

import java.util.Optional;

public class AddStockCommandHandler implements CommandHandler {
    private final ModifiableFundRepository fundRepository;

    public AddStockCommandHandler(ModifiableFundRepository fundRepository) {
        this.fundRepository = fundRepository;
    }

    @Override
    public CommandResult handle(ParsedCommand command) {
        String fundName = command.getArgument(0);
        String stockName = command.getArgument(1);

        if (!fundRepository.getFundByName(fundName).isPresent()) {
            return CommandResult.error(ErrorMessages.FUND_NOT_FOUND);
        }

        fundRepository.addStockToFund(fundName, stockName);
        return CommandResult.success();
    }
}