package io.github.henrymiles3.nighttickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StocksBot {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(StocksBot.class);
        application.run(args);
    }

}
