package bank.domain;

import java.util.*;
import java.util.stream.Collectors;

public class MoneyAccount {

    private final UUID accountId;
    private final List<Transaction> transactions;


    public MoneyAccount() {
        transactions = new ArrayList<>();
        accountId = UUID.randomUUID();
    }

    public MoneyAccount(UUID accountId, List<Transaction> transactions) {
        this.accountId = accountId;
        this.transactions = transactions;
    }

    public int getBalance() {
        int balance = 0;
        for (Transaction transaction : transactions) {
            balance += transaction.getAmount();
        }
        return balance;
    }

    public void changeBalance(int value, Date timestamp) {
        if (isTransactionAvailable(value)) {
            transactions.add(new Transaction(value, timestamp));
        } else {
            throw new NegativeBalanceException("Your balance in is less than zero");
        }
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    private boolean isTransactionAvailable(int value) {
        return (getBalance() + value) >= 0;
    }

    public List<Transaction> getMoneyAccountTransactionList() {
        return makeCopy(transactions);
    }

    private List<Transaction> makeCopy(List<Transaction> transactions) {
        return transactions.stream().map(Transaction::copy).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "MoneyAccount{" +
                "transactions=" + transactions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoneyAccount that = (MoneyAccount) o;
        return Objects.equals(transactions, that.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactions);
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getMoneyAccountId() {
        return accountId;
    }
}
