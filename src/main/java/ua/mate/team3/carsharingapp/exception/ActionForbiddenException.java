package ua.mate.team3.carsharingapp.exception;

public class ActionForbiddenException extends RuntimeException {
    public ActionForbiddenException(String message) {
        super(message);
    }

    public ActionForbiddenException(Throwable e) {
        super(e);
    }

    public ActionForbiddenException(String message, Throwable e) {
        super(message, e);
    }
}
