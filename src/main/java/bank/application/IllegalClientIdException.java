package bank.application;

public class IllegalClientIdException extends Throwable {
    public IllegalClientIdException(String message) {
        super(message);
    }
}
