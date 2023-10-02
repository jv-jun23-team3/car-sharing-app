package ua.mate.team3.carsharingapp.notification;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramNotificationService extends TelegramLongPollingBot implements NotificationService {
    public TelegramNotificationService(String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText()) {
                String text = message.getText();
                Long chatId = message.getChatId();

                if (text.equalsIgnoreCase("/start")) {
                    sendNotification(chatId.toString(), "Here must be intro to bot");
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "car_sharing_project_bot";
    }

    public void sendNotification(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Could not send message");
        }
    }
}
