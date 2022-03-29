package bank.application.exceptions;

public class RepositoryError extends Error {
    public RepositoryError(String message) {
        super(message);
    }
}
