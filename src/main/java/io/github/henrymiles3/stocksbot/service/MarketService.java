package io.github.henrymiles3.stocksbot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.github.henrymiles3.stocksbot.entity.Company;
import io.github.henrymiles3.stocksbot.entity.CompanyRepository;

@Service
public class MarketService {

    private final Random random = new Random();
    private final CompanyRepository companyRepository;

    // Store default stock prices in-memory
    private final Map<String, Double> defaultMarketPrices = new HashMap<>();

    public MarketService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;

        // Initialize default stock starting prices
        defaultMarketPrices.put("STABLE", 50.0);
        defaultMarketPrices.put("ERRATIC", 20.0);
        defaultMarketPrices.put("PUMP", 5.0);
    }

    /**
     * Automatically updates stock prices for default stocks and all player PLCs every 60 seconds.
     */
    @Scheduled(fixedRate = 6)//Change this back to 60000 for production.
    public void fluctuateMarket() {
        // 1. Update default stocks
        for (Map.Entry<String, Double> entry : defaultMarketPrices.entrySet()) {
            String symbol = entry.getKey();
            double newPrice = calculateNewPrice(symbol, entry.getValue(), false);
            defaultMarketPrices.put(symbol, newPrice);
        }

        // 2. Update all player-created companies (PLCs) fetched from the database
        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            // Assuming your Company entity has a stockPrice field and getter/setter
            double currentPrice = company.getStockPrice();
            double newPrice = calculateNewPrice("PLC", currentPrice, true);
            
            company.setStockPrice(newPrice);
            companyRepository.save(company);
        }
    }

    /**
     * Applies volatility rules based on stock type or company type.
     */
    private double calculateNewPrice(String symbol, double currentPrice, boolean isPlayerCompany) {
        double percentChange;

        if (isPlayerCompany) {
            // Player companies can have moderate to high volatility based on player activity
            percentChange = (random.nextDouble() * 0.10) - 0.05; // -5% to +5%
        } else {
            switch (symbol) {
                case "STABLE":
                    percentChange = (random.nextDouble() * 0.03) - 0.015;
                    break;
                case "ERRATIC":
                    percentChange = (random.nextDouble() * 0.165) - 0.08;
                    break;
                case "PUMP":
                    double roll = random.nextDouble();
                    if (roll < 0.15) {
                        percentChange = -(0.15 + random.nextDouble() * 0.20); // Crash
                    } else if (roll < 0.35) {
                        percentChange = 0.20 + random.nextDouble() * 0.30; // Pump
                    } else {
                        percentChange = (random.nextDouble() * 0.20) - 0.10;
                    }
                    break;
                default:
                    percentChange = 0.0;
            }
        }

        double updatedPrice = currentPrice * (1.0 + percentChange);
        if (updatedPrice < 0.10) {
            updatedPrice = 0.10; // Floor price
        }
        return Math.round(updatedPrice * 100.0) / 100.0;
    }

    public double getPrice(String symbol) {
        if (defaultMarketPrices.containsKey(symbol)) {
            return defaultMarketPrices.get(symbol);
        }
        
        // If it's a PLC, check the database
        return companyRepository.findBySymbol(symbol)
                .map(Company::getStockPrice)
                .orElse(0.0);
    }
}