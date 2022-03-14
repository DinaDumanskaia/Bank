package bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankService {

    private final List<Client> bankClients = new ArrayList<>();
    private final DateProvider dateProvider;

    @Autowired
    public BankService(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    public Client createNewClient(String clientID) {
        Client client = new Client(clientID, dateProvider);
        bankClients.add(client);
        return client;
    }

    public boolean clientExists(String clientId) {
        return bankClients.stream()
                .anyMatch(client -> client.getID().equals(clientId));
    }

    public void transferMoney(String sender, String recipient, int value) throws Exception {
        transferMoney(sender, recipient, value, Currency.RUB);
    }

    public void transferMoney(String sender, String recipient, int value, Currency currency) throws Exception {
        checkTransferAbility(sender, recipient);
        makeTransfer(sender, recipient, value, currency);
    }

    private void checkTransferAbility(String sender, String recipient) throws Exception {
        if (!clientExists(sender) || !clientExists(recipient)) {
            throw new Exception("bank.Client not found.");
        }
    }

    private void makeTransfer(String sender, String recipient, int value, Currency currency) throws Exception {
        Client clientFrom = getClientById(sender);
        Client clientTo = getClientById(recipient);
        clientFrom.changeBalance(-1 * value, currency);
        clientTo.changeBalance(value, currency);
    }

    private Client getClientById(String id) {
        return bankClients.stream()
                .filter(client -> client.getID().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("bank.Client not found"));
    }

    public void changeBalance(String id, Currency currency, int value) throws Exception {
        getClientById(id).changeMoneyAccountBalance(currency, value);
    }

    public void changeBalance(String id, int value) throws Exception {
        changeBalance(id, Currency.RUB, value);
    }

    public int getBalance(String id, Currency currency) {
        return getClientById(id).getMoneyAccountBalance(currency);
    }

    public int getBalance(String id) {
        return getBalance(id, Currency.RUB);
    }

    public List<TransactionData> getTransactions(String id) {
        return getClientById(id).getListOfTransactions();
    }

    public List<Client> findAll() {
        return bankClients;
    }
}
