package bank.infrastructure.database;

import bank.application.IllegalClientIdException;
import bank.application.RepositoryError;
import bank.domain.Client;
import bank.application.ClientNotFoundException;
import bank.application.ClientRepository;
import bank.domain.Currency;
import bank.domain.MoneyAccount;
import bank.domain.Transaction;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;

@Service
public class RealClientRepository implements ClientRepository {
    private final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private final Connection connection;
    private final Statement statement;

    public RealClientRepository() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL, "sa", "password");
        statement = connection.createStatement();
    }

    @Override
    public boolean clientExists(UUID clientId) {
        try {
            ResultSet resultSet = getStatement().executeQuery("select *from CLIENTS where id = '" + clientId + "'");
            return resultSet.next();
        } catch (SQLException ex) {
            throw new RepositoryError("Bad service connection");
        }
    }

    private Statement getStatement() throws SQLException {
        return DriverManager.getConnection(DB_URL, "sa", "password").createStatement();
    }

    @Override
    public Client getClientById(UUID clientId) throws IllegalClientIdException {
        if(clientId == null)
            throw new IllegalClientIdException("Client id shouldn't be null or empty");
        try {
            ResultSet resultSet = queryClientData(clientId);
            throwIfNoClient(resultSet.next());
            return new Client(clientId, Map.of(Currency.RUB, createClient(resultSet)));
        } catch (SQLException ex) {
            throw new RepositoryError("Bad service connection");
        }
    }

    private ResultSet queryClientData(UUID clientId) throws SQLException {
        return getStatement().executeQuery("SELECT * FROM CLIENTS C\n" +
                "LEFT JOIN ACCOUNTS A ON A.CLIENT_ID = C.ID\n" +
                "LEFT JOIN TRANSACTIONS TR ON TR.ACCOUNT_ID = A.ACCOUNT_ID \n" +
                "WHERE C.ID = '" + clientId + "'");
    }

    private MoneyAccount createClient(ResultSet resultSet) throws SQLException {
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

    private Date getTransactionDate(ResultSet resultSet) throws SQLException {
        return new Date(resultSet.getTimestamp("TRANSACTION_DATE").getTime());
    }

    private boolean hasTransaction(ResultSet resultSet) throws SQLException {
        return resultSet.getString("TRANSACTION_ID") != null;
    }

    private void throwIfNoClient(boolean hasNext) throws SQLException {
        if (!hasNext) {
            throw new ClientNotFoundException("Client not found");
        }
    }

    @Override
    public void saveClient(Client client) {
        try {
            connection.setAutoCommit(false);
            persistClient(client);
            persistAccount(client);
            persistTransactions(client);
            connection.commit();

        } catch (SQLException ex) {
            throw new RepositoryError("Bad service connection");
        }
    }

    private void persistAccount(Client client) throws SQLException {
        this.statement.execute("MERGE INTO ACCOUNTS VALUES('" + client.getMoneyAccountId(Currency.RUB) + "', '" + client.getID() + "', 'RUB')");
    }

    private void persistClient(Client client) throws SQLException {
        this.statement.execute("MERGE INTO CLIENTS VALUES ('" + client.getID() + "')");
    }

    private void persistTransactions(Client client) throws SQLException {
        for (Transaction transaction : client.getListOfTransactions()) {
            Date date = transaction.getDate();
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "MERGE INTO TRANSACTIONS VALUES('" + transaction.getTransactionId() + "', '" + client.getMoneyAccountId(Currency.RUB) + "', '" + transaction.getAmount() + "', ?)");
            preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
            preparedStatement.execute();
        }
    }

}
