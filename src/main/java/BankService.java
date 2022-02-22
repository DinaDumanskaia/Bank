import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankService {
    private final List<Integer> accountStatement = new ArrayList<>();
    private List<String> bankClients = new ArrayList<>();
    private Map<String, Integer> clientsBalance = new HashMap<>();

    public BankService() {
    }

    public int getBalance(String clientsPhone) {
        return clientsBalance.get(clientsPhone);
    }

    /**
     * changing balance according passing value
     * adding note to account statement
     * @param value a value which changes the balance
     * @throws Exception if balance become less than zero
     */
    public void changeBalance(String clientsPhone, int value) throws Exception {
        int balance = clientsBalance.get(clientsPhone);
        clientsBalance.put(clientsPhone, (balance + value));
        accountStatement.add(value);

        if (balance < 0) {
            throw new Exception("Your balance is less than zero");
        }
    }

    public List<Integer> getAccountStatement(String clientPhone) {
        return accountStatement;
    }

    public void createNewClient(String phoneNumber) {
        bankClients.add(phoneNumber);
        clientsBalance.put(phoneNumber, 0);
    }

    public boolean clientExists(String s) {
        return bankClients.contains(s);
    }
}
