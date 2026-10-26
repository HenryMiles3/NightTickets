package io.github.henrymiles3.stocksbot.discord;

import io.github.henrymiles3.stocksbot.service.ChartService;
import io.github.henrymiles3.stocksbot.service.MarketService;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;

@Component
public class ChartButtonListener extends ListenerAdapter {

    private final MarketService marketService;
    private final ChartService chartService;

    // Track user click timestamps: User ID -> Milliseconds
    private final Map<Long, Long> cooldowns = new ConcurrentHashMap<>();
    private static final long COOLDOWN_MS = 3000; // 3 seconds

    public ChartButtonListener(MarketService marketService, ChartService chartService) {
        this.marketService = marketService;
        this.chartService = chartService;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();

        if (componentId.startsWith("refresh_chart_")) {
            long userId = event.getUser().getIdLong();
            long now = System.currentTimeMillis();

            // 1. Enforce 3-second cooldown
            if (cooldowns.containsKey(userId)) {
                long lastClick = cooldowns.get(userId);
                long timeLeft = (lastClick + COOLDOWN_MS) - now;

                if (timeLeft > 0) {
                    event.reply("⏳ Please wait " + String.format("%.1f", timeLeft / 1000.0) + "s before refreshing again.")
                         .setEphemeral(true)
                         .queue();
                    return;
                }
            }

            // Update cooldown record
            cooldowns.put(userId, now);

            String symbol = componentId.replace("refresh_chart_", "");
            double currentPrice = marketService.getPrice(symbol);

            event.deferEdit().queue(); // Acknowledge button press immediately

            try {
                List<Double> history = List.of(currentPrice * 0.88, currentPrice * 0.94, currentPrice * 0.91, currentPrice);
                File chartFile = chartService.generateStockChart(symbol, history);
                FileUpload fileUpload = FileUpload.fromData(chartFile, "chart.png");

                event.getHook().editOriginal("Updated chart for **" + symbol + "** (Price: $" + currentPrice + ")")
                .setAttachments(fileUpload)
                .setComponents(ActionRow.of(Button.primary("refresh_chart_" + symbol, "Refresh Chart 🔄")))
                .queue();
            } catch (Exception e) {
                event.getHook().sendMessage("Failed to refresh chart.").setEphemeral(true).queue();
            }
        }
    }
}