package bank.infrastructure.database;

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
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RealClientRepository implements ClientRepository {

    @Override
    public boolean clientExists(UUID clientId) {
        var url = "jdbc:h2:tcp://localhost/~/test";

        try {
            var con = DriverManager.getConnection(url, "sa", "password");
            var stm = con.createStatement();
            ResultSet resultSet = stm.executeQuery("select *from CLIENTS where id = '" + clientId + "'");

            if (resultSet.next()) return true;
        } catch (SQLException ex) {

            var lgr = Logger.getLogger(RealClientRepository.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public Client getClientById(UUID clientId) {
        var url = "jdbc:h2:tcp://localhost/~/test";

        try {
            var con = DriverManager.getConnection(url, "sa", "password");
            var stm = con.createStatement();


            ResultSet resultSet = stm.executeQuery("SELECT * FROM CLIENTS C\n" +
                    "LEFT JOIN ACCOUNTS A ON A.CLIENT_ID = C.ID\n" +
                    "LEFT JOIN TRANSACTIONS TR ON TR.ACCOUNT_ID = A.ACCOUNT_ID \n" +
                    "WHERE C.ID = '" + clientId + "'");

            List<Transaction> transactions = new ArrayList<>();
            Currency currency = Currency.RUB;
            UUID moneyAccountId = null;
            if (!resultSet.next()) {
                throw new ClientNotFoundException("Client not found");
            }
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

            var lgr = Logger.getLogger(RealClientRepository.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        throw new ClientNotFoundException("Client not found");
    }

    @Override
    public void saveClient(Client client) {
        var url = "jdbc:h2:tcp://localhost/~/test";

        try {
            var con = DriverManager.getConnection(url, "sa", "password");
            var stm = con.createStatement();
            stm.execute("MERGE INTO CLIENTS VALUES ('" + client.getID() + "')");
            UUID account_id = client.getMoneyAccountId(Currency.RUB);
            stm.execute("MERGE INTO ACCOUNTS VALUES('" + account_id + "', '" + client.getID() + "', 'RUB')");
//            stm.execute("INSERT INTO TRANSACTIONS VALUES('" + UUID.randomUUID() + "', '" + account_id + "', '200', ?)");
            for (Transaction transaction : client.getListOfTransactions()) {
                Date date = transaction.getDate();
                PreparedStatement preparedStatement = con.prepareStatement(
                        "MERGE INTO TRANSACTIONS VALUES('" + transaction.getTransactionId() + "', '" + account_id + "', '" + transaction.getAmount() + "', ?)");
                preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
                preparedStatement.execute();
            }

//            if (rs.next()) {
//
//                System.out.println(rs.getInt(1));
//            }

        } catch (SQLException ex) {

            var lgr = Logger.getLogger(RealClientRepository.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
