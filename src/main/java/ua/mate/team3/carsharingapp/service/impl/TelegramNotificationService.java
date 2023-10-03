package ua.mate.team3.carsharingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.bots.TelegramBot;
import ua.mate.team3.carsharingapp.service.NotificationService;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot arsenTelegramBot;
    private final TelegramBot bogdanTelegramBot;
    private final TelegramBot romaTelegramBot;
    private final TelegramBot vitaliyTelegramBot;

    @Override
    public void sendNotification(String message) {
        arsenTelegramBot.sendMessage(arsenTelegramBot.getChatId(), message);
        bogdanTelegramBot.sendMessage(bogdanTelegramBot.getChatId(), message);
        romaTelegramBot.sendMessage(bogdanTelegramBot.getChatId(), message);
        vitaliyTelegramBot.sendMessage(vitaliyTelegramBot.getChatId(), message);
    }
}
