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

    public void createNewClient(String id) {
        Client client = new Client(id, dateProvider);
        bankClients.add(client);
    }

    public boolean clientExists(String phoneNumber) {
        return bankClients.stream()
                .anyMatch(client -> client.getPhone().equals(phoneNumber));
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

    private Client getClientByPhone(String phone) {
        return bankClients.stream()
                .filter(client -> client.getPhone().equals(phone))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("bank.Client not found"));
    }

    public void changeBalance(String phone, Currency currency, int value) throws Exception {
        getClientByPhone(phone).changeMoneyAccountBalance(currency, value);
    }

    public void changeBalance(String phone, int value) throws Exception {
        changeBalance(phone, Currency.RUB, value);
    }

    public int getBalance(String phone, Currency currency) {
        return getClientByPhone(phone).getMoneyAccountBalance(currency);
    }

    public int getBalance(String phone) {
        return getBalance(phone, Currency.RUB);
    }

    public List<TransactionData> getTransactions(String phone) {
        return getClientByPhone(phone).getListOfTransactions();
    }
}
