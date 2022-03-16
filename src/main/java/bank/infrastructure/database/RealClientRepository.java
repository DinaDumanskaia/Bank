package bank.infrastructure.database;

import bank.domain.Client;
import bank.application.ClientNotFoundException;
import bank.application.ClientRepository;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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

            return new Client();
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
