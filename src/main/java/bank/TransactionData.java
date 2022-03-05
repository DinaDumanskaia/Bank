package bank;

import java.util.Date;

public class TransactionData {

    private final int value;
    private final Date date;

    public TransactionData(int value, Date date) {
        this.value = value;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }

    TransactionData copy() {
        return new TransactionData(value, date);
    }
}
