package ua.mate.team3.carsharingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.mate.team3.carsharingapp.bots.TelegramBot;
import ua.mate.team3.carsharingapp.service.NotificationService;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot bot;

    @Override
    public void sendNotification(String string) {

    }
}
