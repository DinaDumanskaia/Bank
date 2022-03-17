package bank.infrastructure.database;

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
    private Statement statement;

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
    public Client getClientById(UUID clientId) {
        try {
            ResultSet resultSet = getStatement().executeQuery("SELECT * FROM CLIENTS C\n" +
                    "LEFT JOIN ACCOUNTS A ON A.CLIENT_ID = C.ID\n" +
                    "LEFT JOIN TRANSACTIONS TR ON TR.ACCOUNT_ID = A.ACCOUNT_ID \n" +
                    "WHERE C.ID = '" + clientId + "'");

            List<Transaction> transactions = new ArrayList<>();
            Currency currency = Currency.RUB;
            UUID moneyAccountId;
            throwIfNoClient(resultSet.next());
            do {
                moneyAccountId = UUID.fromString(resultSet.getString("ACCOUNT_ID"));
                if (resultSet.getTimestamp("TRANSACTION_DATE") == null) {
                    continue;
                }
                Date transactionDate = new Date(resultSet.getTimestamp("TRANSACTION_DATE").getTime());
                int transaction = resultSet.getInt("AMOUNT");
                UUID transactionId = UUID.fromString(resultSet.getString("TRANSACTION_ID"));
                transactions.add(new Transaction(transactionId, transaction, transactionDate));
            } while (resultSet.next());
            return new Client(clientId, Map.of(currency, new MoneyAccount(moneyAccountId, transactions)));
        } catch (SQLException ex) {
            throw new RepositoryError("Bad service connection");
        }
    }
    private void throwIfNoClient(boolean hasNext) throws SQLException {
        if (!hasNext) {
            throw new ClientNotFoundException("Client not found");
        }
    }

    @Override
    public void saveClient(Client client) {
        try {
            persistClient(client);
            persistAccount(client);
            persistTransactions(client);
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
