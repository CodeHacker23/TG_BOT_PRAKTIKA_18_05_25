package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class SimpleApp {
    public static void main(String[] args) {
        System.out.println("Запуск упрощенного приложения...");
        SpringApplication.run(SimpleApp.class, args);
        System.out.println("Упрощенное приложение запущено успешно!");
        
    }
} 