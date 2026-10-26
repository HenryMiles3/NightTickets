package io.github.henrymiles3.stocksbot.discord;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Component;

import io.github.henrymiles3.stocksbot.service.ChartService;
import io.github.henrymiles3.stocksbot.service.MarketService;
import io.github.henrymiles3.stocksbot.service.TradingService;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;

@Component
public class CommandManager extends ListenerAdapter{

    private final TradingService tradingService;
    private final MarketService marketService;
    private final ChartService chartService;

    public CommandManager(TradingService tradingService, MarketService marketService, ChartService chartService) {
        this.tradingService = tradingService;
        this.marketService = marketService;
        this.chartService = chartService;
    }

    @Override 
    public void onReady(ReadyEvent event) {
        event.getJDA().updateCommands().addCommands(
            Commands.slash("price", "Check current stock price")
                    .addOption(OptionType.STRING, "symbol", "Stock symbol (e.g. STABLE, ERRATIC, PUMP)", true),
            Commands.slash("buy", "Buy stock shares")
                    .addOption(OptionType.STRING, "symbol", "Stock symbol", true)
                    .addOption(OptionType.INTEGER, "quantity", "Number of shares", true),
            Commands.slash("sell", "Sell stock shares")
                    .addOption(OptionType.STRING, "symbol", "Stock symbol", true)
                    .addOption(OptionType.INTEGER, "quantity", "Number of shares", true),
            Commands.slash("chart", "View price trend graph for a stock")
                    .addOption(OptionType.STRING, "symbol", "Stock symbol", true)
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String discordId = event.getUser().getId();
        String command = event.getName();

        switch (command) {
            case "price" -> {
                String symbol = event.getOption("symbol").getAsString().toUpperCase();
                double price = marketService.getPrice(symbol);
                if (price == 0.0) {
                    event.reply("Stock symbol `" + symbol + "` not found.").setEphemeral(true).queue();
                } else {
                    event.reply("The current price of **" + symbol + "** is `$" + price + "`.").queue();
                }
            }
            case "buy" -> {
                String symbol = event.getOption("symbol").getAsString().toUpperCase();
                int qty = event.getOption("quantity").getAsInt();
                double price = marketService.getPrice(symbol);
                if (price == 0.0) {
                    event.reply("Stock symbol `" + symbol + "` not found.").setEphemeral(true).queue();
                    return;
                }
                String result = tradingService.buyStock(discordId, symbol, qty, price);
                event.reply(result).queue();
            }
            case "sell" -> {
                String symbol = event.getOption("symbol").getAsString().toUpperCase();
                int qty = event.getOption("quantity").getAsInt();
                double price = marketService.getPrice(symbol);
                if (price == 0.0) {
                    event.reply("Stock symbol `" + symbol + "` not found.").setEphemeral(true).queue();
                    return;
                }
                String result = tradingService.sellStock(discordId, symbol, qty, price);
                event.reply(result).queue();
            }
            case "chart" -> {
                String symbol = event.getOption("symbol").getAsString().toUpperCase();
                double currentPrice = marketService.getPrice(symbol);
                if (currentPrice == 0.0) {
                    event.reply("Stock symbol `" + symbol + "` not found.").setEphemeral(true).queue();
                    return;
                }

                // Defer reply since generating the chart image takes a moment
                event.deferReply().queue(hook -> {
                    try {
                        // Dummy price history list for illustration (you can retrieve real history points from MarketService)
                        List<Double> history = List.of(currentPrice * 0.9, currentPrice * 0.95, currentPrice * 0.92, currentPrice);
                        File chartFile = chartService.generateStockChart(symbol, history);

                        FileUpload fileUpload = FileUpload.fromData(chartFile, "chart.png");
                        hook.sendFiles(fileUpload)
                        .setComponents(ActionRow.of(Button.primary("refresh_chart_" + symbol, "Refresh Chart 🔄")))
                        .queue();
                    } catch (Exception e) {
                        hook.sendMessage("Failed to generate chart image.").queue();
                    }
                });
            }
        }
    }
}
