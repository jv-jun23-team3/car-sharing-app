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
    private final TelegramBot olegTelegramBot;

    @Override
    public void sendNotification(String message) {
        if (arsenTelegramBot.getChatId() != null) {
            arsenTelegramBot.sendMessage(arsenTelegramBot.getChatId(), message);
        }
        if (bogdanTelegramBot.getChatId() != null) {
            bogdanTelegramBot.sendMessage(bogdanTelegramBot.getChatId(), message);
        }
        if (romaTelegramBot.getChatId() != null) {
            romaTelegramBot.sendMessage(bogdanTelegramBot.getChatId(), message);
        }
        if (vitaliyTelegramBot.getChatId() != null) {
            vitaliyTelegramBot.sendMessage(vitaliyTelegramBot.getChatId(), message);
        }
        if (olegTelegramBot.getChatId() != null) {
            olegTelegramBot.sendMessage(olegTelegramBot.getChatId(), message);
        }
    }
}
