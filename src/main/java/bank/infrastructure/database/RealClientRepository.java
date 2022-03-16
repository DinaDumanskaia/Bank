package bank.infrastructure.database;

import bank.domain.Client;
import bank.application.ClientNotFoundException;
import bank.application.ClientRepository;
import bank.domain.Currency;
import bank.domain.MoneyAccount;
import bank.domain.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RealClientRepository implements ClientRepository {

    @Override
    public boolean clientExists(UUID clientId) {
        var url = "jdbc:h2:tcp://localhost/~/test";

        try {
            var con = DriverManager.getConnection(url, "sa", "password");
            var stm = con.createStatement();
            ResultSet resultSet = stm.executeQuery("select * from CLIENTS where id = '" + clientId + "'");

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
            resultSet.next();
            Date transactionDate = new Date(resultSet.getTimestamp("TRANSACTION_DATE").getTime());
            int transaction = resultSet.getInt("AMOUNT");
            Currency currency = Currency.valueOf(resultSet.getString("CURRENCY"));

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(new Transaction(transaction, transactionDate));
            Client client = new Client(clientId, Map.of(currency, new MoneyAccount(transactions)));

            return client;
//            if (rs.next()) {
//
//                System.out.println(rs.getInt(1));
//            }

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
             stm.execute("INSERT INTO CLIENTS VALUES ('" + client.getID() + "')");
            UUID account_id = UUID.randomUUID();
            stm.execute("INSERT INTO ACCOUNTS VALUES('" + account_id + "', '" + client.getID() + "', 'RUB')");
//            stm.execute("INSERT INTO TRANSACTIONS VALUES('" + UUID.randomUUID() + "', '" + account_id + "', '200', ?)");
            Date date = client.getListOfTransactions().get(0).getDate();
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO TRANSACTIONS VALUES('" + UUID.randomUUID() + "', '" + account_id + "', '200', ?)");
            preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
            preparedStatement.execute();

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
