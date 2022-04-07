package bank.infrastructure.database;

import bank.application.adapters.ClientRepository;
import bank.application.exceptions.ClientNotFoundException;
import bank.application.exceptions.IllegalClientIdException;
import bank.application.exceptions.RepositoryError;
import bank.domain.Client;
import bank.domain.Currency;
import bank.domain.MoneyAccount;
import bank.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Date;
import java.util.*;

@Repository
public class RealClientRepository implements ClientRepository {
    private final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private final Connection connection;
    private final Statement statement;
    PreparedStatement preparedStatement;

    public RealClientRepository() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL, "sa", "password");
        statement = connection.createStatement();
    }

    @Override
    public boolean clientExists(UUID clientId) {
        if (clientId == null) {
            throw new  IllegalClientIdException("INCORRECT ID");
        }
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM CLIENTS WHERE id = ?");
            preparedStatement.setString(1, clientId.toString());
            return preparedStatement.executeQuery().next();
        } catch (SQLException ex) {
            throw new RepositoryError("BAD SERVICE CONNECTION");
        }
    }

    private Statement getStatement() throws SQLException {
        return DriverManager.getConnection(DB_URL, "sa", "password").createStatement();
    }

    @Override
    public Client getClientById(UUID clientId) {
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM CLIENTS C\n" +
                    "LEFT JOIN ACCOUNTS A ON A.CLIENT_ID = C.ID\n" +
                    "LEFT JOIN TRANSACTIONS TR ON TR.ACCOUNT_ID = A.ACCOUNT_ID \n" +
                    "WHERE C.ID = ? ");
            preparedStatement.setString(1, clientId.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            throwIfNoClient(resultSet.next());


            EnumMap<Currency, MoneyAccount> moneyAccountMap = new EnumMap<>(Currency.class);
            do {
                if (hasTransaction(resultSet)) {
                    Currency currency = Currency.valueOf(getCurrency(resultSet));
                    MoneyAccount moneyAccountForCurrency = moneyAccountMap.getOrDefault(currency, new MoneyAccount());
                    moneyAccountForCurrency.addTransaction(new Transaction(getTransactionId(resultSet), getAmount(resultSet), getTransactionDate(resultSet)));
                    moneyAccountMap.put(currency, moneyAccountForCurrency);
                }
            } while (resultSet.next());

            return new Client(clientId, moneyAccountMap);
        } catch (SQLException ex) {
            throw new RepositoryError("BAD SERVICE CONNECTION");
        }
    }

    private ResultSet queryClientData(UUID clientId) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT * FROM CLIENTS C\n" +
                "LEFT JOIN ACCOUNTS A ON A.CLIENT_ID = C.ID\n" +
                "LEFT JOIN TRANSACTIONS TR ON TR.ACCOUNT_ID = A.ACCOUNT_ID \n" +
                "WHERE C.ID = ? ");
        preparedStatement.setString(1, clientId.toString());
        return preparedStatement.executeQuery();
    }

    private MoneyAccount createMoneyAccount(ResultSet resultSet) throws SQLException {
        return new MoneyAccount(getAccountId(resultSet), getTransactionList(resultSet));
    }

    private List<Transaction> getTransactionList(ResultSet resultSet) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        do {
            if (hasTransaction(resultSet))
                transactions.add(createTransaction(resultSet));
        } while (resultSet.next());
        return transactions;
    }

    private UUID getAccountId(ResultSet resultSet) throws SQLException {
        return UUID.fromString(resultSet.getString("ACCOUNT_ID"));
    }

    private Transaction createTransaction(ResultSet resultSet) throws SQLException {
        return new Transaction(getTransactionId(resultSet), getAmount(resultSet), getTransactionDate(resultSet));
    }

    private UUID getTransactionId(ResultSet resultSet) throws SQLException {
        return UUID.fromString(resultSet.getString("TRANSACTION_ID"));
    }

    private int getAmount(ResultSet resultSet) throws SQLException {
        return resultSet.getInt("AMOUNT");
    }

    private String getCurrency(ResultSet resultSet) throws SQLException {
        return resultSet.getString("CURRENCY");
    }

    private Date getTransactionDate(ResultSet resultSet) throws SQLException {
        return new Date(resultSet.getTimestamp("TRANSACTION_DATE").getTime());
    }

    private boolean hasTransaction(ResultSet resultSet) throws SQLException {
        return resultSet.getString("TRANSACTION_ID") != null;
    }

    private void throwIfNoClient(boolean hasNext) throws SQLException {
        if (!hasNext) {
            throw new ClientNotFoundException("CLIENT NOT FOUND");
        }
    }

    @Override
    public void saveClient(Client client) {
        try {
            connection.setAutoCommit(false);
            persistClient(client);
            for (Currency currency : getUsedCurrencyByClient(client)) {
                persistAccount(client, currency);
            }
            persistTransactions(client.getMoneyAccounts());
            connection.commit();

        } catch (SQLException ex) {
            throw new RepositoryError("BAD SERVICE CONNECTION");
        }
    }

    private Set<Currency> getUsedCurrencyByClient(Client client) {
        // извлечь все транзакции из маниаккаунта и посмотреть какие валюты используются. их и добавить в сет
        // улыбаца
        Set<Currency> set = new HashSet<>();
        Map<Currency, MoneyAccount> map = client.getMoneyAccounts();
        for (Map.Entry<Currency, MoneyAccount> moneyAccountEntry : map.entrySet()) {
            set.add(moneyAccountEntry.getKey());
        }
        return set;
    }


    private void persistAccount(Client client, Currency currency) throws SQLException {
        preparedStatement = connection.prepareStatement("MERGE INTO ACCOUNTS VALUES( ?, ?, ?)");
        preparedStatement.setString(1, client.getMoneyAccountId(currency).toString());
        preparedStatement.setString(2, client.getID().toString());
        preparedStatement.setString(3, currency.name());
        preparedStatement.execute();
    }

    private void persistClient(Client client) throws SQLException {
        preparedStatement = connection.prepareStatement("MERGE INTO CLIENTS VALUES (?)");
        preparedStatement.setString(1, client.getID().toString());
        preparedStatement.execute();
     }

    private void persistTransactions( Map<Currency, MoneyAccount> moneyAccounts) throws SQLException {
        for (Map.Entry<Currency, MoneyAccount> moneyAccountEntry : moneyAccounts.entrySet()) {
            MoneyAccount moneyAccount = moneyAccountEntry.getValue();

            for (Transaction transaction : moneyAccount.getMoneyAccountTransactionList()) {
                Date date = transaction.getDate();
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "MERGE INTO TRANSACTIONS VALUES(?, ?, ?, ?)");
                preparedStatement.setString(1, transaction.getTransactionId().toString());
                preparedStatement.setString(2, moneyAccount.getAccountId().toString());
                preparedStatement.setInt(3, transaction.getAmount());
                preparedStatement.setTimestamp(4, new Timestamp(date.getTime()));
                preparedStatement.execute();
            }
        }
    }
}
