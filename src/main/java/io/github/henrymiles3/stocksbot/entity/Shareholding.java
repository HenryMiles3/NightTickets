package io.github.henrymiles3.stocksbot.entity;


import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "shareholdings", uniqueConstraints = 
    {@UniqueConstraint(columnNames = {"player_id", "stock_symbol"})}
)
public class Shareholding {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    //The player who owns the shares
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private String stockSymbol;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double averageBuyPrice;

    public Shareholding() {}

    public Shareholding(Player player, String stockSymbol, int quantity, double averageBuyPrice) {
        this.player = player;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
    }

    public UUID getId() {return id;}

    public Player getPlayer() {return player;}
    public void setPlayer(Player player) {this.player = player;}

    public String getStockSymbol() {return stockSymbol;}
    public void setStockSymbol(String stockSymbol) {this.stockSymbol = stockSymbol;}

    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}

    public double getAverageBuyPrice() {return averageBuyPrice;}
    public void setAverageBuyPrice(double averageBuyPrice) {this.averageBuyPrice = averageBuyPrice;}
}
