import java.util.*;

public class BankService {
    private final Map<String, List<TransactionData>> accountStatement = new HashMap<>();
    private final List<String> bankClients = new ArrayList<>();
    private final Map<String, ClientBalances> clientsBalance = new HashMap<>();

    public BankService() {
    }

    public int getBalance(String clientsPhone, Currency currency) {
        return clientsBalance.get(clientsPhone).getBalanceByCurrency(currency);
    }

    public int getBalance(String clientsPhone) {
        return getBalance(clientsPhone, Currency.RUB);
    }

    public void changeBalance(String clientsPhone, int value) throws Exception {
        changeBalance(clientsPhone, value, Currency.RUB);
    }

    public void changeBalance(String clientsPhone, int value, Currency currency) throws Exception {
        //сохраняю баланс по ID Клиента в указанной валюте
        saveBalance(clientsPhone, value, currency);
        //получаю список транзакций по ID клиента в указанной валюте
        List<TransactionData> listOfCurrentClientStatements = accountStatement.get(clientsPhone);
        //добавляем в список новую транзакцию в указанной валюте
        listOfCurrentClientStatements.add(new TransactionData(value, new Date(), currency));

    }

    private void saveBalance(String clientsPhone, int value, Currency currency) throws Exception {

        //clientsBalance.put(clientsPhone, (clientsBalance.get(clientsPhone) + value));
        //у клиента взять баланс соответсвующей валюты
        int balanceValue = getBalance(clientsPhone, currency);
        //изменить баланс на указанное значение
        balanceValue += value;
        //проверить, что баланс не отрицательный
        if (balanceValue < 0) {
            throw new Exception("Your balance in " + currency.name() + " is less than zero");
        } else {
            updateBalance(clientsPhone, balanceValue, currency);
        }
            //если не отрицательный, то сохранить
            //если отрицательный, выбросить исключение (баланс не меняется)


//        //по ID проверяем, что у клиента не отрицательный баланс в указанной валюте
//        if (clientsBalance.get(clientsPhone) < 0) {
//            throw new Exception("Your balance is less than zero");
//        }
    }

    private void updateBalance(String clientsPhone, int balanceValue, Currency currency) {
        ClientBalances clientBalances = clientsBalance.get(clientsPhone);
        clientBalances.changeBalance(balanceValue, currency);
    }

    public List<TransactionData> getAccountStatement(String clientPhone) {
        return accountStatement.get(clientPhone);
    }

    public void createNewClient(String phoneNumber) {
        bankClients.add(phoneNumber);
        clientsBalance.put(phoneNumber, new ClientBalances());
        accountStatement.put(phoneNumber, new ArrayList<>());
    }

    public boolean clientExists(String s) {
        return bankClients.contains(s);
    }

    public void transferMoney(String sender, String recipient, int value) throws Exception {
        //добавить проверку на доступность суммы
        changeBalance(sender, value * -1);
        changeBalance(recipient, value);
    }
}
