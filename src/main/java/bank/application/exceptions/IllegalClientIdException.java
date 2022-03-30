package bank.application.exceptions;

public class IllegalClientIdException extends RuntimeException {
    public IllegalClientIdException(String message) {
        super(message);
    }
}
