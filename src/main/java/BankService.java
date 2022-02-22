import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankService {
    private final Map<String, List<Integer>> accountStatement = new HashMap<>();
    private List<String> bankClients = new ArrayList<>();
    private Map<String, Integer> clientsBalance = new HashMap<>();

    public BankService() {
    }

    public int getBalance(String clientsPhone) {
        return clientsBalance.get(clientsPhone);
    }

    public void changeBalance(String clientsPhone, int value) throws Exception {
        int balance = clientsBalance.get(clientsPhone);
        clientsBalance.put(clientsPhone, (balance + value));
        List<Integer> listOfCurrentClientStatements = accountStatement.get(clientsPhone);
        listOfCurrentClientStatements.add(value);

        if (clientsBalance.get(clientsPhone) < 0) {
            throw new Exception("Your balance is less than zero");
        }
    }

    public List<Integer> getAccountStatement(String clientPhone) {
        return accountStatement.get(clientPhone);
    }

    public void createNewClient(String phoneNumber) {
        bankClients.add(phoneNumber);
        clientsBalance.put(phoneNumber, 0);
        accountStatement.put(phoneNumber, new ArrayList<>());
    }

    public boolean clientExists(String s) {
        return bankClients.contains(s);
    }

    public void transferMoney(String sender, String recipient, int value) throws Exception {
        changeBalance(sender, value * -1);
        changeBalance(recipient, value);
    }
}
