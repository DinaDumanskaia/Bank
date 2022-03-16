package bank.domain;

import java.util.Date;

public class Transaction {

    private final int amount;
    private final Date date;

    public Transaction(int value, Date date) {
        this.amount = value;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    Transaction copy() {
        return new Transaction(amount, date);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", date=" + date +
                '}';
    }
}
