package bank.domain;

import java.util.*;

public class Client {
    private final UUID id;
    private final Map<Currency, MoneyAccount> moneyAccounts;

    public Client() {
        id = UUID.randomUUID();
        moneyAccounts = new HashMap<>();
    }

    public Client(UUID id, Map<Currency, MoneyAccount> moneyAccounts) {
        this.id = id;
        this.moneyAccounts = moneyAccounts;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id) && Objects.equals(moneyAccounts, client.moneyAccounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, moneyAccounts);
    }
}


