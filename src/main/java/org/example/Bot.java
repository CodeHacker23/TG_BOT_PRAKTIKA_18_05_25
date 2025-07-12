package org.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.example.PersonageBase.getRandomPersonage;

/**
 * Bot — главный дирижёр, связующее звено между Telegram и всеми сервисами.
 * Именно здесь решается, кто и когда будет страдать от багов, а кто — получать фоточки и карточки.
 *
 * Здесь происходит:
 * - Приём всех апдейтов от Telegram
 * - Маршрутизация команд и сообщений
 * - Вызов StoryStartService для старта и создания персонажа
 * - Вызов MessageHandlerService для остального трэша
 *
 * Если ты решишь добавить бизнес-логику прямо сюда — знай, Архитектор уже выехал за тобой.
 *
 * Пример использования:
 *   Bot бот = ...;
 *   бот.onUpdateReceived(update); // и понеслась душа в рай
 */
@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot { // класс бота
    // Все сервисы, которые нужны боту. Если их станет больше 10 — пора делать рефакторинг.
    private final org.example.Service service;
    private final ArrayListStoryService arrayListStoryService;
    private final MessageHandlerService messageHandlerService;
    private final UserService userService;
    private final PersonageCreationService personageCreationService;
    private final StoryStartService storyStartService;

    /**
     * Проверка, есть ли у пользователя персонаж
     * @param user — сущность пользователя
     * @return true, если персонаж уже создан
     *
     * Пример:
     *   if (hasCharacter(user)) { ... }
     */
    private boolean hasCharacter(UserEntity user) {
        return user != null && user.getCharacterType() != null && !user.getCharacterType().isEmpty();
    }

    /**
     * Главный метод: обработка любого апдейта от Telegram
     * @param update — апдейт от Telegram
     *
     * Здесь происходит вся магия маршрутизации:
     * - Если пришёл callbackQuery с data "create_personage" — запускаем создание персонажа
     * - Если пришло текстовое сообщение:
     *     - /start — запускаем StoryStartService.handleStart
     *     - Если пользователь ждёт ввода имени — передаём в StoryStartService.handleCharacterNameInput
     *     - Всё остальное — в MessageHandlerService (и пусть он разбирается)
     *
     * Если что-то пойдёт не так — смотри логи и готовься к дебагу.
     */
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("[Bot] onUpdateReceived() — получен апдейт: " + update);
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Long userId = update.getCallbackQuery().getFrom().getId();
            System.out.println("[Bot] CallbackQuery: data=" + data + ", chatId=" + chatId + ", userId=" + userId);

            if ("create_personage".equals(data)) {
                System.out.println("[Bot] Пользователь нажал 'Создать персонажа'.");
                storyStartService.handleCreatePersonage(this, chatId, userId);
                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();
            System.out.println("[Bot] Message: text='" + text + "', chatId=" + chatId + ", userId=" + userId);

            // Обрабатываем команду /start отдельно
            if ("/start".equals(text)) {
                System.out.println("[Bot] Пользователь отправил /start.");
                storyStartService.handleStart(this, chatId, userId);
                return;
            }

            // Обработка ввода имени персонажа
            UserEntity user = storyStartService.userService.getUserByTgId(userId);
            if (user != null && "AWAITING_CHARACTER_NAME".equals(user.getState())) {
                System.out.println("[Bot] Пользователь вводит имя персонажа: '" + text + "'.");
                storyStartService.handleCharacterNameInput(this, chatId, userId, text);
                return;
            }

            // Обрабатываем все остальные сообщения через MessageHandlerService
            try {
                System.out.println("[Bot] Передаём сообщение в MessageHandlerService.");
                messageHandlerService.handleMessage(this, update.getMessage());
            } catch (TelegramApiException e) {
                System.err.println("[Bot] Ошибка в MessageHandlerService: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Имя бота (Telegram username)
     * @return строка с именем
     */
    @Override
    public String getBotUsername() {
        return "Collection_bot";
    }

    /**
     * Токен бота (Telegram API token)
     * @return строка с токеном
     *
     * Никогда не выкладывай токен в открытый доступ, иначе твой бот быстро станет чужим!
     */
    @Override
    public String getBotToken() { //
        return "";
    }
}

