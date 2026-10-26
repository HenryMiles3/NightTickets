package io.github.henrymiles3.stocksbot.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.henrymiles3.stocksbot.entity.Company;
import io.github.henrymiles3.stocksbot.entity.CompanyRepository;
import io.github.henrymiles3.stocksbot.entity.Player;
import jakarta.transaction.Transactional;

@Service
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final PlayerService playerService;

    public CompanyService(CompanyRepository companyRepository, PlayerService playerService) {
        this.companyRepository = companyRepository;
        this.playerService = playerService;
    }

    /**
     * Creates a new PLC if the player meets the $50m net worth requirement.
     * Starts with 100 shares entirely owned by the founder.
     */
    @Transactional
    public Company createCompany(String discordId, String companyName, String companySymbol) {
        Player owner = playerService.getOrCreatePlayer(discordId);

        if (owner.getNetWorth() < 50_000_000) {
            throw new IllegalArgumentException("You need a net worth of at least $50 million to create a company.");
        }

        //Check if company name is already taken:
        if (companyRepository.findByName(companyName).isPresent()) {
            throw new IllegalArgumentException("Company name already taken.");
        }

        Company company = new Company(companySymbol, companyName, 1, 00, owner);
        return companyRepository.save(company);
    }

    @Transactional
    public Company updateBalance(UUID companyId, double amount){
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new IllegalArgumentException("Company not found."));

        double newBalance = company.getCorporateBalance() + amount;
        if(newBalance < 0) {
            throw new IllegalArgumentException("Insufficient funds for this transaction.");
        }
        company.setCorporateBalance(newBalance);
        return companyRepository.save(company);
    }
}
