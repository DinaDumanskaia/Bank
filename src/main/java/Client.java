import java.util.HashMap;
import java.util.Map;

public class Client {
    private final String phoneNumber;
    private final Map<Currency, MoneyAccount> moneyAccounts = new HashMap<>();
    private final DateProvider dateProvider;

    protected MoneyAccount getMoneyAccount(Currency currency) {
        if (moneyAccounts.get(currency) == null) {
            moneyAccounts.put(currency, new MoneyAccount(dateProvider));
        }
        return moneyAccounts.get(currency);
    }

    protected MoneyAccount getMoneyAccount() {
        return getMoneyAccount(Currency.RUB);
    }

    public Client(String phoneNumber, DateProvider dateProvider) {
        this.phoneNumber = phoneNumber;
        this.dateProvider = dateProvider;
    }

    public String getPhone() {
        return phoneNumber;
    }

    void changeBalance(int value, Currency currency) throws Exception {
        getMoneyAccount(currency).changeBalance(value);
    }
}


