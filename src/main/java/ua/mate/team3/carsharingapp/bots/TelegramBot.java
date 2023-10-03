package ua.mate.team3.carsharingapp.bots;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.mate.team3.carsharingapp.exception.NotificationException;

public class TelegramBot extends TelegramLongPollingBot {
    private static final String WELCOME_MESSAGE = ". Welcome to the Car Sharing Bot. "
            + "With this bot, you will be able to receive notifications about the status of users' rentals.";
    private static final String RENTAL_COMPLETED_MESSAGE =
            ", your rental successfully completed.";
    private static final String RENTAL_ENDED_MESSAGE =
            ", your rental successfully closed.";
    private static final String START_COMMAND = "/start";
    private static final String COMMAND_NOT_FOUND_MESSAGE = "Sorry, command not found";
    private final String botUserName;
    @Getter
    private Long chatId;
    private List<BotCommand> commands;

    public TelegramBot(String botToken, String botUserName) {
        super(botToken);
        this.botUserName = botUserName;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            if (message.equals(START_COMMAND)) {
                startCommandReceived(chatId, getName(update));
            } else {
                sendMessage(chatId, COMMAND_NOT_FOUND_MESSAGE);
            }
        }
    }

    @PostConstruct
    private void initMenu() {
        commands = new ArrayList<>();
        commands.add(new BotCommand(START_COMMAND, "send a welcome message"));
        try {
            this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new NotificationException("Can't initialize command menu", e);
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + WELCOME_MESSAGE;
        sendMessage(chatId, answer);
    }

    private void newRentalCommandReceived(Long chatId, String name) {
        String answer = name + RENTAL_COMPLETED_MESSAGE;
        sendMessage(chatId, answer);
    }

    private void endRentalCommandReceived(Long chatId, String name) {
        String answer = name + RENTAL_ENDED_MESSAGE;
        sendMessage(chatId, answer);
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new NotificationException("Can't send message. PLease, try again" + e);
        }
    }

    private String getName(Update update) {
        return update.getMessage().getChat().getFirstName();
    }
}
