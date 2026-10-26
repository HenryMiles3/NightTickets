package io.github.henrymiles3.stocksbot.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String discordId;

    private double balance = 100;
    private double netWorth = 100;

    public Player() {}
    
    public Player(String discordId) {
        this.discordId = discordId;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public String getDiscordId() {return discordId;}
    public void setDiscordId(String discordId) {this.discordId = discordId;}

    public double getBalance() {return balance;}
    public void setBalance(double balance) {this.balance = balance;}

    public double getNetWorth() {return netWorth;}
    public void setNetWorth(double netWorth) {this.netWorth = netWorth;}
}
