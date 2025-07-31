package com.example.geektrust;

import com.example.geektrust.command.CommandType;
import com.example.geektrust.domain.Portfolio;
import com.example.geektrust.handler.*;
import com.example.geektrust.repository.JsonFundRepository;
import com.example.geektrust.repository.ModifiableFundRepository;
import com.example.geektrust.service.CommandExecutor;
import com.example.geektrust.service.OverlapCalculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Application {
    private final CommandExecutor commandExecutor;

    public Application(String stockDataJsonPath) {
        JsonFundRepository jsonRepository = new JsonFundRepository(stockDataJsonPath);
        ModifiableFundRepository modifiableRepository = new ModifiableFundRepository(jsonRepository);
        Portfolio portfolio = new Portfolio();
        OverlapCalculator overlapCalculator = new OverlapCalculator();

        commandExecutor = new CommandExecutor();
        commandExecutor.registerHandler(CommandType.CURRENT_PORTFOLIO, 
            new CurrentPortfolioCommandHandler(portfolio, modifiableRepository));
        commandExecutor.registerHandler(CommandType.CALCULATE_OVERLAP, 
            new CalculateOverlapCommandHandler(portfolio, modifiableRepository, overlapCalculator));
        commandExecutor.registerHandler(CommandType.ADD_STOCK, 
            new AddStockCommandHandler(modifiableRepository));
    }

    public void run(List<String> commandLines) {
        List<CommandResult> results = commandExecutor.executeCommands(commandLines);
        
        for (CommandResult result : results) {
            if (!result.isSuccess() && result.getErrorMessage() != null) {
                System.out.println(result.getErrorMessage());
            } else if (result.hasOutput()) {
                for (String output : result.getOutputs()) {
                    System.out.println(output);
                }
            }
        }
    }

    public void run(String inputFilePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(inputFilePath));
        run(lines);
    }
}