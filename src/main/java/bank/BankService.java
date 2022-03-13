package bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.ClientDTO;

import java.util.ArrayList;
import java.util.Collections;
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

    public ClientDTO createNewClient() throws Exception {
        final String id = String.valueOf(UUID.randomUUID());

        Client client = new Client(id, dateProvider);
        bankClients.add(client);

        return new ClientDTO(id, 0, Collections.emptyList());
    }

    public boolean clientExists(String phoneNumber) {
        return bankClients.stream()
                .anyMatch(client -> client.getID().equals(phoneNumber));
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
        Client clientFrom = getClientByPhone(sender);
        Client clientTo = getClientByPhone(recipient);
        clientFrom.changeBalance(-1 * value, currency);
        clientTo.changeBalance(value, currency);
    }

    private Client getClientByPhone(String id) {
        return bankClients.stream()
                .filter(client -> client.getID().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("bank.Client not found"));
    }

    public void changeBalance(String id, Currency currency, int value) throws Exception {
        getClientByPhone(id).changeMoneyAccountBalance(currency, value);
    }

    public void changeBalance(String id, int value) throws Exception {
        changeBalance(id, Currency.RUB, value);
    }

    public int getBalance(String id, Currency currency) {
        return getClientByPhone(id).getMoneyAccountBalance(currency);
    }

    public int getBalance(String id) {
        return getBalance(id, Currency.RUB);
    }

    public List<TransactionData> getTransactions(String id) {
        return getClientByPhone(id).getListOfTransactions();
    }

    public List<Client> findAll() {
        return bankClients;
    }
}
