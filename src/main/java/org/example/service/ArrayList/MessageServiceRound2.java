package org.example.service.ArrayList;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.entity.PersonageEntity;
import org.example.model.entity.UserEntity;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.example.service.PhotoService.PhotoReam;

/**
 * 🎮 СЕРВИС СООБЩЕНИЙ ДЛЯ РАУНДА 2
 *
 * Этот класс отвечает за создание всех сообщений раунда 2:
 * - Начало раунда 2 (перенесено из ArrayListTheoryService)
 * - Реплики персонажей в раунде 2
 * - Вывод статов персонажа
 * - Боевые сообщения раунда 2
 *
 * 🎯 ЦЕЛЬ: Централизовать все сообщения раунда 2 в одном месте
 * для упрощения разработки и поддержки.
 *
 * 📝 ПРИНЦИПЫ:
 * - Простота и читаемость кода
 * - Переиспользование логики (buildStatsLine)
 * - Подробные комментарии
 * - Один класс = один раунд
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageServiceRound2 {
    private final UserService userService;
    private final StatService statService;
    private final ArrayListTheoryService arrayListTheoryService;



    /**
     * Создает фото-сообщение с объявлением раунда 2.
     *
     * @param chatId — ID чата пользователя
     * @return SendPhoto — фото-сообщение о раунде 2 с подписью
     */
    public SendPhoto createRound2Message(Long chatId) {
        log.debug("MessageServiceRound2: Создание фото-сообщения о раунде 2 для chatId={}", chatId);

        UserEntity user = userService.getUserByTgId(chatId);

        // Получаем пользователя и персонажа проверяем что он не null
        if(user == null || user.getPersonage() == null){
            log.warn("MessageServiceRound2: Пользователь или персонаж не найден для раунда 2 для chatId={}", chatId);
            // В случае ошибки возвращаем фото без статов
            return PhotoReam.createRound2PhotoMessage(chatId, "Ошибка: персонаж не найден.");
        }
        
        //получаем нашего персонажа из БД
        PersonageEntity entity = user.getPersonage();

        // Формируем строку статов
        String statsLine = buildStatsLine(entity);

        log.debug("MessageServiceRound2: Фото-сообщение о раунде 2 создано");
        return PhotoReam.createRound2PhotoMessage(chatId, statsLine);
    }

    /**
     * 💬 РЕПЛИКА ИТЕРАТОРИУСА ПЕРЕД РАУНДОМ 2
     * <p>
     * Дополнительное сообщение с репликой от Итераториуса о сложности раунда 2.
     */
    public SendMessage createIteratoriusRound2Intro(Long chatId) {
        log.debug("MessageServiceRound2: Создание вступительной реплики Итераториуса для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);

        sendMessage.setText("""
               *Итераториус:*
                _"Неплохо справился с первым испытанием! Но это была лишь разминка..."_
                
                _"Раунд 2 покажет, действительно ли ты понял принципы работы ArrayList._
                _Противники стали хитрее, а ставки — выше."_
                
                _"Помни: знание структуры данных — твое главное оружие!"_
                
                ⚔️ **Приготовься к настоящему испытанию!**
                """);

        return sendMessage;
    }

    /**
     * 🎯 СООБЩЕНИЕ С ХАРАКТЕРИСТИКАМИ ВРАГА РАУНДА 2
     * <p>
     * Информация о противнике второго раунда (усиленный Аррейн).
     */
    public SendMessage createRound2EnemyInfo(Long chatId) {
        log.debug("MessageServiceRound2: Создание информации о враге раунда 2 для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);

        sendMessage.setText("""
                👹 **ПРОТИВНИК: АРРЕЙН** 👹
                
                📊 **Характеристики:**
                🛡️ HP: 100 (Было150)
                 
                
                💀 **Новые способности:**
                • **Capacity Overflow** — критический урон при переполнении
                • **Index Shift Storm** — массовая атака на все позиции
                • **Memory Leak** — урон со временем
                
                """);

        return sendMessage;
    }

    /**
     * 🏆 СООБЩЕНИЕ О ПОБЕДЕ В РАУНДЕ 2
     * <p>
     * Поздравление игрока с прохождением раунда 2.
     */
    public SendMessage createRound2VictoryMessage(Long chatId) {
        log.debug("MessageServiceRound2: Создание сообщения о победе в раунде 2 для chatId={}", chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode("Markdown");
        sendMessage.setChatId(chatId);

        // Получаем обновленные статы для показа прогресса
        UserEntity user = userService.getUserByTgId(chatId);
        String statsLine = "";
        if (user != null && user.getPersonage() != null) {
            statsLine = "\n" + buildStatsLine(user.getPersonage());
        }

        sendMessage.setText("""
                🎊 **РАУНД 2 ПРОЙДЕН!** 🎊
                
                💬 **Итераториус (впечатлен):**
                _"Превосходно! Ты не просто выжил — ты доминировал!"_
                
                _"Твое понимание ArrayList стало глубже. Теперь ты видишь не только методы, но и их внутреннюю работу."_
                
                _"Аррейн 2.0 был серьезным противником, но ты справился!"_
                %s
                
                🎯 **Готов к еще большим вызовам?**
                """.formatted(statsLine));

        return sendMessage;
    }
























    /**
     * 📊 ФОРМИРОВАНИЕ СТРОКИ СТАТИСТИКИ (перенесено из ArrayListTheoryService)
     *
     * Создает читаемую строку с характеристиками персонажа.
     * Метод перенесен без изменений для сохранения совместимости.
     *
     * @param entity — сущность персонажа
     * @return String — отформатированная строка со статистикой
     */
    private String buildStatsLine(PersonageEntity entity) {
        log.debug("MessageServiceRound2: Формирование строки статов для персонажа типа: {}", entity.getCharacterType());

        String type = entity.getCharacterType();
        String money = String.valueOf(entity.getCurrency());
        if (money.endsWith(".0")) money = money.substring(0, money.length() - 2); // убираем .0 если не нужно

        String statsLine;
        switch (type) {
            case "Personage1":
                statsLine = "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |📊 Аналитика: " + (entity.getAnalytics() != null ? entity.getAnalytics() : 0) +
                        " |🛡 Сопротивление дедлайну: " + (entity.getDeadlineResistance() != null ? entity.getDeadlineResistance() : 0);
                break;
            case "Personage2":
                statsLine = "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |😁 Юмор: " + (entity.getHumor() != null ? entity.getHumor() : 0) +
                        " |💬 Навыки коммуникации: " + (entity.getCommunication() != null ? entity.getCommunication() : 0);
                break;
            case "Personage3":
                statsLine = "|🏆Level: " + entity.getLevel() +
                        " |⚡️Энергия: " + entity.getEnergy() +
                        " |⭐️Очки достижения: " + entity.getAchievementPoints() +
                        " |💲Деньги: " + money +
                        " |💾 Точность кода: " + (entity.getCodeAccuracy() != null ? entity.getCodeAccuracy() : 0) +
                        " |⚙️ Оптимизация: " + (entity.getOptimization() != null ? entity.getOptimization() : 0);
                break;
            default:
                statsLine = "|⚡️Энергия: " + entity.getEnergy();
                break;
        }

        log.debug("MessageServiceRound2: Строка статов сформирована: {}", statsLine);
        return statsLine;
    }

    private int safe(Integer value){
        return value != null ? value : 0;
    }

}
