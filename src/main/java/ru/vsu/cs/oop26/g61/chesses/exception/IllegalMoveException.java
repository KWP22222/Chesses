package ru.vsu.cs.oop26.g61.chesses.exception;

/**
 * Исключение, выбрасываемое при попытке сделать недопустимый шахматный ход.
 */
public class IllegalMoveException extends Exception{
    private final String reason;

    public IllegalMoveException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public IllegalMoveException(String reason) {
        super("Недопустимый ход: " + reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
