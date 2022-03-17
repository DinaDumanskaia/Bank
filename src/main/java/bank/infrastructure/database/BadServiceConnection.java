package bank.infrastructure.database;

public class BadServiceConnection extends RuntimeException {
    public BadServiceConnection(String message) {
        super(message);
    }
}
