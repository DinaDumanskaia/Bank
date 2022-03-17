package bank.domain;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final UUID transactionId;
    private final int amount;
    private final Date date;

    public Transaction(UUID transactionId, int amount, Date date) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.date = date;
    }

    public Transaction(int value, Date date) {
        this.amount = value;
        this.date = date;
        transactionId = UUID.randomUUID();
    }

    public Date getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    Transaction copy() {
        return new Transaction(transactionId, amount, date);
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

    public UUID getTransactionId() {
        return transactionId;
    }
}
