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

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String symbol; // e.g., custom PLC ticker

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double stockPrice;

    // Corporate bank account separate from player's personal balance
    @Column(nullable = false)
    private double corporateBalance;

    // The player who founded/owns this PLC
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Player owner;

    public Company() {
    }

    public Company(String symbol, String name, double stockPrice, double corporateBalance, Player owner) {
        this.symbol = symbol;
        this.name = name;
        this.stockPrice = stockPrice;
        this.corporateBalance = corporateBalance;
        this.owner = owner;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public double getCorporateBalance() {
        return corporateBalance;
    }

    public void setCorporateBalance(double corporateBalance) {
        this.corporateBalance = corporateBalance;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}