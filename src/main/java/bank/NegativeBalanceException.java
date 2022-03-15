package bank;

public class NegativeBalanceException extends RuntimeException {


    public NegativeBalanceException(String errorMessage) {
        super(errorMessage);
    }
}
