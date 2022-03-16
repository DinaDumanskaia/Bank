package bank.domain;

import java.util.Date;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return amount == that.amount && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, date);
    }
}
