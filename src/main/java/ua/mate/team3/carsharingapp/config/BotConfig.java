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

    @Value(value = "${bogdan.telegram.token}")
    private String bogdanBotToken;
    @Value(value = "${bogdan.telegram.bot.name}")
    private String bogdanBotUserName;

    @Value(value = "${oleg.telegram.token}")
    private String olegBotToken;
    @Value(value = "${oleg.telegram.bot.name}")
    private String olegBotUserName;

    @Value(value = "${vitaliy.telegram.token}")
    private String vitaliyBotToken;
    @Value(value = "${vitaliy.telegram.bot.name}")
    private String vitaliyBotUserName;

    @Value(value = "${roma.telegram.token}")
    private String romaBotToken;
    @Value(value = "${roma.telegram.bot.name}")
    private String romaBotUserName;

    @Bean
    public TelegramBot arsenTelegramBot() {
        return new TelegramBot(arsenBotToken, arsenBotUserName);
    }

    @Bean
    public TelegramBot olegTelegramBot() {
        return new TelegramBot(olegBotToken, olegBotUserName);
    }

    @Bean
    public TelegramBot bogdanTelegramBot() {
        return new TelegramBot(bogdanBotToken, bogdanBotUserName);
    }

    @Bean
    public TelegramBot romaTelegramBot() {
        return new TelegramBot(romaBotToken, romaBotUserName);
    }

    @Bean
    public TelegramBot vitaliyTelegramBot() {
        return new TelegramBot(vitaliyBotToken, vitaliyBotUserName);
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(arsenTelegramBot());
            telegramBotsApi.registerBot(bogdanTelegramBot());
            telegramBotsApi.registerBot(olegTelegramBot());
            telegramBotsApi.registerBot(vitaliyTelegramBot());
            telegramBotsApi.registerBot(romaTelegramBot());
        } catch (TelegramApiException e) {
            throw new NotificationException("Can't initialize bot ", e);
        }
    }
}
