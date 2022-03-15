package bank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MoneyAccount {
    private final List<TransactionData> transactions = new ArrayList<>();

    public int getBalance() {
        int balance = 0;
        for (TransactionData transaction : transactions) {
            balance += transaction.getValue();
        }
        return balance;
    }

    public void changeBalance(int value, Date timestamp) {
        if (isTransactionAvailable(value)) {
            transactions.add(new TransactionData(value, timestamp));
        } else {
            throw new NegativeBalanceException("Your balance in is less than zero");
        }
    }

    private boolean isTransactionAvailable(int value) {
        return (getBalance() + value) >= 0;
    }

    public List<TransactionData> getMoneyAccountTransactionList() {
        return makeCopy(transactions);
    }

    private List<TransactionData> makeCopy(List<TransactionData> transactions) {
        return transactions.stream().map(TransactionData::copy).collect(Collectors.toList());
    }

}
