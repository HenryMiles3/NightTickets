package io.github.henrymiles3.stocksbot.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareholdingRepository extends JpaRepository<Shareholding, UUID> {
    
    //Finds all stocks owned by a specific player
    List<Shareholding> findByPlayer(Player player);

    //Finds a specific stock holding for a player.
    Optional<Shareholding> findByPlayerAndStockSymbol(Player player, String stockSymbol);
}
