package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

//@SpringBootApplication
@EnableJpaRepositories("org.example.repository")
@EntityScan("org.example.entity")
//@EnableTelegramBots
public class TestApp {
    public static void main(String[] args) {
        System.out.println("Запуск приложения...");
        SpringApplication.run(TestApp.class, args);
        System.out.println("Приложение запущено успешно!");
    }
} 