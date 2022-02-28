import java.util.ArrayList;
import java.util.List;

public class BankService {

    private final List<Client> bankClients = new ArrayList<>();
    private final DateProvider dateProvider;

    public BankService(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    public void createNewClient(String phoneNumber) {
        Client client = new Client(phoneNumber, dateProvider);
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
        if (!clientExists(sender) || !clientExists(recipient)) {
            throw new Exception("Client not found.");
        } else {
            Client clientFrom = getClientByPhone(sender);
            Client clientTo = getClientByPhone(recipient);
            clientFrom.getClientBalances().changeBalance(value * -1, currency);
            clientTo.getClientBalances().changeBalance(value, currency);
        }
    }

    public Client getClientByPhone(String phone) {
        return bankClients.stream()
                .filter(client -> client.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
    }
}
