package bank.application;

import bank.domain.Client;
import bank.domain.Currency;
import bank.domain.TransactionData;
import bank.infrastructure.database.FakeClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BankService {

    private final DateProvider dateProvider;
    private final FakeClientRepository clientRepository = new FakeClientRepository();

    @Autowired
    public BankService(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    public Client createNewClient() {
        Client client = new Client();
        clientRepository.saveClient(client);
        return client;
    }

    public boolean clientExists(UUID clientId) {
        return clientRepository.clientExists(clientId);
    }

    public void transferMoney(UUID sender, UUID recipient, int value) throws Exception {
        transferMoney(sender, recipient, value, Currency.RUB);
    }

    public void transferMoney(UUID sender, UUID recipient, int value, Currency currency) throws Exception {
        checkTransferAbility(sender, recipient);
        makeTransfer(sender, recipient, value, currency);
    }

    private void checkTransferAbility(UUID sender, UUID recipient) throws Exception {
        if (!clientRepository.clientExists(sender) || !clientRepository.clientExists(recipient)) {
            throw new Exception("bank.domain.Client not found.");
        }
    }

    private void makeTransfer(UUID sender, UUID recipient, int value, Currency currency) {
        Client clientFrom = clientRepository.getClientById(sender);
        Client clientTo = clientRepository.getClientById(recipient);
        clientFrom.changeBalance(-1 * value, currency, dateProvider.getDate());
        clientTo.changeBalance(value, currency, dateProvider.getDate());
    }

    public Client getClientById(UUID id) {
        return clientRepository.getClientById(id);
    }

    public void changeBalance(UUID id, Currency currency, int value) {
        clientRepository.getClientById(id).changeBalance(value, currency, dateProvider.getDate());
    }

    public void changeBalance(UUID id, int value) {
        changeBalance(id, Currency.RUB, value);
    }

    public int getBalance(UUID id, Currency currency) {
        return clientRepository.getClientById(id).getMoneyAccountBalance(currency);
    }

    public int getBalance(UUID id) {
        return getBalance(id, Currency.RUB);
    }

    public List<TransactionData> getTransactions(UUID id) {
        return clientRepository.getClientById(id).getListOfTransactions();
    }

}
