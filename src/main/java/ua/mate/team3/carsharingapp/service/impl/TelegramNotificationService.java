package ua.mate.team3.carsharingapp.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.bots.TelegramBot;
import ua.mate.team3.carsharingapp.service.NotificationService;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final List<TelegramBot> botList;

    @Override
    public void sendNotification(String message) {
        for (TelegramBot adminBot : botList) {
            if (adminBot.getChatId() != null) {
                adminBot.sendMessage(adminBot.getChatId(), message);
            }
        }
    }
}
