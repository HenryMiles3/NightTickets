package io.github.henrymiles3.stocksbot.entity;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    // Find a company by its ticker symbol (e.g., "AAPL")
    Optional<Company> findBySymbol(String symbol);

    // Find a company by its full company name
    Optional<Company> findByName(String name);
}