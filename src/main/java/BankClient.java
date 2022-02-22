import java.util.ArrayList;
import java.util.List;

public class BankClient implements Account {
    private double balance;
    private final List<Double> accountStatement = new ArrayList<>();

    public BankClient() {
    }

    public double getBalance() {
        return balance;
    }

    /**
     * changing balance according passing value
     * adding note to account statement
     * @param value a value which changes the balance
     * @throws Exception if balance become less than zero
     */
    public void changeBalance(double value) throws Exception {
        balance += value;
        accountStatement.add(value);

        if (balance < 0) {
            throw new Exception("Your balance is less than zero");
        }
    }

    public List<Double> getAccountStatement() {
        return accountStatement;
    }

    @Override
    public void setAccountCurrency(Currency currency) {

    }

    @Override
    public Currency getAccountCurrency() {
        return null;
    }
}
