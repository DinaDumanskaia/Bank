package bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private final String id;
    private final Map<Currency, MoneyAccount> moneyAccounts = new HashMap<>();
    private final DateProvider dateProvider;

    private MoneyAccount getMoneyAccount(Currency currency) {
        if (moneyAccounts.get(currency) == null) {
            moneyAccounts.put(currency, new MoneyAccount(dateProvider));
        }
        return moneyAccounts.get(currency);
    }

    private MoneyAccount getMoneyAccount() {
        return getMoneyAccount(Currency.RUB);
    }

    public Client(String id, DateProvider dateProvider) {
        this.id = id;
        this.dateProvider = dateProvider;
    }

    public String getID() {
        return id;
    }

    void changeBalance(int value, Currency currency) throws Exception {
        getMoneyAccount(currency).changeBalance(value);
    }

    public void changeMoneyAccountBalance(Currency currency, int value) throws Exception {
        getMoneyAccount(currency).changeBalance(value);
    }

    public int getMoneyAccountBalance(Currency currency) {
        return getMoneyAccount(currency).getBalance();
    }

    public List<TransactionData> getListOfTransactions() {
        return getMoneyAccount().getMoneyAccountTransactionList();
    }
}


