package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MoneyAccount {
    private final DateProvider dateProvider;
    private final List<TransactionData> transactions = new ArrayList<>();

    public MoneyAccount(DateProvider dateProvider) {this.dateProvider = dateProvider;}

    public int getBalance() {
        int balance = 0;
        for (TransactionData transaction : transactions) {
            balance += transaction.getValue();
        }
        return balance;
    }

    public void changeBalance(int value) throws Exception {
        if (isTransactionAvailable(value)) {
            transactions.add(new TransactionData(value, dateProvider.getDate()));
        } else {
            throw new Exception("Your balance in is less than zero");
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
