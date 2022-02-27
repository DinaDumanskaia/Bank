import java.util.HashMap;
import java.util.Map;

public class ClientBalances {
    Map<Currency, Integer> balance = new HashMap<>();

    public int getBalanceByCurrency(Currency currency) {
        return balance.computeIfAbsent(currency, k -> 0);
    }

    public void changeBalance(int balanceValue, Currency currency) {
        balance.put(currency, balanceValue);
    }
}
