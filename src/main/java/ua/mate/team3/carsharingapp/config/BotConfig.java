package ua.mate.team3.carsharingapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.mate.team3.carsharingapp.bots.TelegramBot;
import ua.mate.team3.carsharingapp.exception.NotificationException;

@Configuration
public class BotConfig {
    @Value(value = "${arsen.telegram.token}")
    private String arsenBotToken;
    @Value(value = "${arsen.telegram.bot.name}")
    private String arsenBotUserName;

    @Bean
    public TelegramBot arsenTelegramBot() {
        return new TelegramBot(arsenBotToken, arsenBotUserName);
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(arsenTelegramBot());
        } catch (TelegramApiException e) {
            throw new NotificationException("Can't initialize bot ", e);
        }
    }
}
