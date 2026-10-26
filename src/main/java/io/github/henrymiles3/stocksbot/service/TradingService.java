package io.github.henrymiles3.stocksbot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.henrymiles3.stocksbot.entity.Player;
import io.github.henrymiles3.stocksbot.entity.PlayerRepository;
import io.github.henrymiles3.stocksbot.entity.Shareholding;
import io.github.henrymiles3.stocksbot.entity.ShareholdingRepository;
import jakarta.transaction.Transactional;

@Service
public class TradingService {
    private final PlayerRepository playerRepository;
    private final ShareholdingRepository shareholdingRepository;

    public TradingService(PlayerRepository playerRepository, ShareholdingRepository shareholdingRepository) {
        this.playerRepository = playerRepository;
        this.shareholdingRepository = shareholdingRepository;
    }

    @Transactional
    public String buyStock(String discordId, String stockSymbol, int quantity, double pricePerShare) {
        if (quantity <= 0) {
            return "Quantity must be greater than zero.";
        }

        double totalCost = quantity * pricePerShare;

        Player player = playerRepository.findByDiscordId(discordId)
                .orElseGet(() -> playerRepository.save(new Player(discordId)));

        if (player.getBalance() < totalCost) {
            return "Insufficient funds to complete this purchase.";
        }

        player.setBalance(player.getBalance() - totalCost);
        playerRepository.save(player);

        Optional<Shareholding> existingShareholding = shareholdingRepository.findByPlayerAndStockSymbol(player, stockSymbol);

        if (existingShareholding.isPresent()) {
            Shareholding holding = existingShareholding.get();
            
            int newTotalQuantity = holding.getQuantity() + quantity;
            double totalSpent = (holding.getAverageBuyPrice() * holding.getQuantity()) + totalCost;
            double newAveragePrice = totalSpent / newTotalQuantity;

            holding.setQuantity(newTotalQuantity);
            holding.setAverageBuyPrice(newAveragePrice);
            shareholdingRepository.save(holding);
        }else{
            Shareholding newHolding = new Shareholding(player, stockSymbol, quantity, pricePerShare);
            shareholdingRepository.save(newHolding);
        }

        return String.format("Successfully purchased %d shares of %s at $%.2f each. Total cost: $%.2f", quantity, stockSymbol, pricePerShare, totalCost);
    }

    public List<Shareholding> getPlayerPortfolio(Player player) {
        return shareholdingRepository.findByPlayer(player);
    }

    @Transactional
    public String sellStock(String discordId, String stockSymbol, int quantity, double currentPricePerShare) {
        if (quantity <= 0) {
            return "Quantity must be greater than zero.";
        }

        // 1. Find the player
        Player player = playerRepository.findByDiscordId(discordId)
                .orElse(null);
        if (player == null) {
            return "Player profile not found.";
        }

        // 2. Find the player's shareholding for this stock
        Optional<Shareholding> optionalHolding = shareholdingRepository.findByPlayerAndStockSymbol(player, stockSymbol);
        if (optionalHolding.isEmpty()) {
            return String.format("You do not own any shares of %s.", stockSymbol);
        }

        Shareholding holding = optionalHolding.get();

        // 3. Check if the player has enough shares to sell
        if (holding.getQuantity() < quantity) {
            return String.format("You only own %d shares of %s, so you cannot sell %d.", 
                    holding.getQuantity(), stockSymbol, quantity);
        }

        double totalRevenue = quantity * currentPricePerShare;

        // 4. Credit the cash to the player's balance
        player.setBalance(player.getBalance() + totalRevenue);
        playerRepository.save(player);

        // 5. Update or remove the shareholding record
        int remainingQuantity = holding.getQuantity() - quantity;
        if (remainingQuantity == 0) {
            // Sold everything, remove the record
            shareholdingRepository.delete(holding);
        } else {
            // Sold a portion, update remaining quantity
            holding.setQuantity(remainingQuantity);
            shareholdingRepository.save(holding);
        }

        return String.format("Successfully sold %d shares of %s for $%.2f!", quantity, stockSymbol, totalRevenue);
    }
    
}
