package bank.domain;

import java.util.*;

public class Client {
    private final UUID id = UUID.randomUUID();
    private final Map<Currency, MoneyAccount> moneyAccounts = new HashMap<>();

    private MoneyAccount getMoneyAccount(Currency currency) {
        if (moneyAccounts.get(currency) == null) {
            moneyAccounts.put(currency, new MoneyAccount());
        }
        return moneyAccounts.get(currency);
    }

    private MoneyAccount getMoneyAccount() {
        return getMoneyAccount(Currency.RUB);
    }

    public UUID getID() {
        return id;
    }

    public void changeBalance(int value, Currency currency, Date timestamp) {
        getMoneyAccount(currency).changeBalance(value, timestamp);
    }

    public int getMoneyAccountBalance(Currency currency) {
        return getMoneyAccount(currency).getBalance();
    }

    public List<Transaction> getListOfTransactions() {
        return getMoneyAccount().getMoneyAccountTransactionList();
    }

    public int getBalance() {
        return getMoneyAccountBalance(Currency.RUB);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", moneyAccounts=" + moneyAccounts +
                '}';
    }
}


