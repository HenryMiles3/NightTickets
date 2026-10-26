package io.github.henrymiles3.stocksbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StocksBotApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(StocksBotApplication.class);
        application.run(args);
    }

}
