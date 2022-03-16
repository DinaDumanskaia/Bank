package bank.domain;

import java.util.Date;

public class TransactionData {

    private final int amount;
    private final Date date;

    public TransactionData(int value, Date date) {
        this.amount = value;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    TransactionData copy() {
        return new TransactionData(amount, date);
    }
}
