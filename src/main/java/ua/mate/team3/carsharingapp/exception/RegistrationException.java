package ua.mate.team3.carsharingapp.exception;

public class RegistrationException extends Exception {
    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable e) {
        super(message, e);
    }
}
