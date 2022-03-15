package bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BankService {

    private final List<Client> bankClients = new ArrayList<>();
    private final DateProvider dateProvider;

    @Autowired
    public BankService(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    public Client createNewClient() {
        Client client = new Client(dateProvider);
        bankClients.add(client);
        return client;
    }

    public boolean clientExists(UUID clientId) {
        return bankClients.stream()
                .anyMatch(client -> client.getID().equals(clientId));
    }

    public void transferMoney(UUID sender, UUID recipient, int value) throws Exception {
        transferMoney(sender, recipient, value, Currency.RUB);
    }

    public void transferMoney(UUID sender, UUID recipient, int value, Currency currency) throws Exception {
        checkTransferAbility(sender, recipient);
        makeTransfer(sender, recipient, value, currency);
    }

    private void checkTransferAbility(UUID sender, UUID recipient) throws Exception {
        if (!clientExists(sender) || !clientExists(recipient)) {
            throw new Exception("bank.Client not found.");
        }
    }

    private void makeTransfer(UUID sender, UUID recipient, int value, Currency currency) throws Exception {
        Client clientFrom = getClientById(sender);
        Client clientTo = getClientById(recipient);
        clientFrom.changeBalance(-1 * value, currency);
        clientTo.changeBalance(value, currency);
    }

    private Client getClientById(UUID id) {
        return bankClients.stream()
                .filter(client -> client.getID().equals(id))
                .findFirst()
                .orElseThrow(() -> new ClientNotFoundException("bank.Client not found"));
    }

    public void changeBalance(UUID id, Currency currency, int value) throws Exception {
        getClientById(id).changeMoneyAccountBalance(currency, value);
    }

    public void changeBalance(UUID id, int value) throws Exception {
        changeBalance(id, Currency.RUB, value);
    }

    public int getBalance(UUID id, Currency currency) {
        return getClientById(id).getMoneyAccountBalance(currency);
    }

    public int getBalance(UUID id) {
        return getBalance(id, Currency.RUB);
    }

    public List<TransactionData> getTransactions(UUID id) {
        return getClientById(id).getListOfTransactions();
    }

    public Client getClient(UUID id) {
        return getClientById(id);
    }
}
