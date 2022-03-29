package bank.application.exceptions;

public class IllegalClientIdException extends Throwable {
    public IllegalClientIdException(String message) {
        super(message);
    }
}
