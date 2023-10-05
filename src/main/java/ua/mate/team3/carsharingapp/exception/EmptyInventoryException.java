package ua.mate.team3.carsharingapp.exception;

public class EmptyInventoryException extends RuntimeException {
    public EmptyInventoryException(String message) {
        super(message);
    }

    public EmptyInventoryException(Throwable e) {
        super(e);
    }

    public EmptyInventoryException(String message, Throwable e) {
        super(message, e);
    }
}
