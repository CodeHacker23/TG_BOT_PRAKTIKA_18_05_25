package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * App — точка входа в приложение (main-класс).
 * Здесь запускается Spring Boot и вся магия DI, конфигов и автосканирования.
 *
 * Как расширять:
 *   - Не пихай сюда бизнес-логику — только запуск!
 *   - Если нужно что-то выполнить при старте — используй @PostConstruct или ApplicationRunner.
 *
 * Пример запуска:
 *   public static void main(String[] args) {
 *       SpringApplication.run(App.class, args);
 *   }
 *
 * Юмор: если начнёшь писать логику в main — Архитектор лично напишет тебе в Telegram.
 */
@SpringBootApplication
public class App {
    public static void main(String[] args ) {
        SpringApplication.run(App.class, args);
        System.out.println("[App] Приложение успешно запущено! Если что-то не работает — смотри логи.");
    }
}
