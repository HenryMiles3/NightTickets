package io.github.henrymiles3.stocksbot.config;

import java.util.EventListener;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
public class JdaConfig {

    @Value("${discord.token}")
    private String botToken;

    @Bean
    public JDA jda(List<Object> allBeans) throws InterruptedException {
        
        JDABuilder builder = JDABuilder.createDefault(botToken)
            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);

        for(Object bean : allBeans) {
            if (bean instanceof ListenerAdapter listenerAdapter) {
                System.out.println("[JDA] Registered event listener: "+ listenerAdapter.getClass().getSimpleName());
                builder.addEventListeners(listenerAdapter);
            }
        }

        JDA jda = builder.build();
        jda.awaitReady();
        System.out.println("[JDA] Discord bot ready!");
        return jda;
    }
}
