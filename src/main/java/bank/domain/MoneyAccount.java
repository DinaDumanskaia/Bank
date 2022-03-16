package bank.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MoneyAccount {
    private final List<Transaction> transactions;

    public MoneyAccount() {
        transactions = new ArrayList<>();
    }

    public MoneyAccount(List<Transaction> transactions) {
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
}
