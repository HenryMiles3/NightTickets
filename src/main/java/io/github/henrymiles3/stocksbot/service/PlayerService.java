package io.github.henrymiles3.stocksbot.service;

import org.springframework.stereotype.Service;

import io.github.henrymiles3.stocksbot.entity.Player;
import io.github.henrymiles3.stocksbot.entity.PlayerRepository;
import jakarta.transaction.Transactional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Finds an existing player by their Discord ID, or automatically registers them 
     * with the starting $100 balance if they are playing for the first time.
     */
    @Transactional
    public Player getOrCreatePlayer(String discordId) {
        return playerRepository.findByDiscordId(discordId).orElseGet(() -> {
            Player newPlayer = new Player(discordId);
            return playerRepository.save(newPlayer);
        });
    }

    @Transactional
    public Player updateBalance(String discordId, double amount) {

        Player player = getOrCreatePlayer(discordId);

        double newBalance = player.getBalance() + amount;
        if(newBalance < 0) {
            throw new IllegalArgumentException("Insufficient funds for this transaction.");
        }

        player.setBalance(newBalance);
        // Update net worth as well
        player.setNetWorth(player.getNetWorth() + amount);
        return playerRepository.save(player);
    }
}