import java.util.Date;

public class TransactionData {

    private final int value;
    private final Date date;

    private final Currency currency;

    public TransactionData(int value, Date date, Currency currency) {
        this.value = value;
        this.date = date;
        this.currency = currency;
    }

    public Date getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }

    public Currency getCurrency() {
        return currency;
    }
}
