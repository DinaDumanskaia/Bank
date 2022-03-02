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
        checkTransferAbility(sender, recipient);
        makeTransfer(sender, recipient, value, currency);
    }

    private void checkTransferAbility(String sender, String recipient) throws Exception {
        if (!clientExists(sender) || !clientExists(recipient)) {
            throw new Exception("Client not found.");
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
                .orElseThrow(() -> new RuntimeException("Client not found"));
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
        return makeCopy(getClientByPhone(phone).getMoneyAccountTransactionList());
    }

    private List<TransactionData> makeCopy(List<TransactionData> transactionList) {
        List<TransactionData> list = new ArrayList<>();
        for (TransactionData transactionData : transactionList) {
            list.add(transactionData.copy());
        }
        return list;
    }
}
