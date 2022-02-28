import java.util.*;

public class BalancesBox {
    private final List<TransactionData> transactionList = new ArrayList<>();
    private final Map<Currency, Integer> allBalances = new HashMap<>();

    public int getBalanceByCurrency(Currency currency) {
        return allBalances.computeIfAbsent(currency, k -> 0);
    }

    public int getBalance() {
        return getBalanceByCurrency(Currency.RUB);
    }

    public void changeBalance(int value, Currency currency) throws Exception {
        int balanceValue;
        if (allBalances.get(currency) == null) {
            balanceValue = 0;
        } else {
            balanceValue = allBalances.get(currency);
        }

        balanceValue += value;
        if (balanceValue < 0) {
            throw new Exception("Your balance in " + currency.name() + " is less than zero");
        } else {
            allBalances.put(currency, balanceValue);
            transactionList.add(new TransactionData(value, new Date(), currency));
        }
    }

    public void changeBalance(int value) throws Exception {
        changeBalance(value, Currency.RUB);
    }

    //запрашиваем операции по счету
    public List<TransactionData> getTransactionList() {
        return transactionList;
    }
}
