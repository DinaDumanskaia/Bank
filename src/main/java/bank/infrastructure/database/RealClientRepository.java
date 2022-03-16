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
            ResultSet resultSet = stm.executeQuery("select * from TEST where id = '" + clientId + "'");

            if (resultSet.next()) return true;
        } catch (SQLException ex) {

            var lgr = Logger.getLogger(RealClientRepository.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public Client getClientById(UUID id) {
        throw new ClientNotFoundException("Client not found");
    }

    @Override
    public void saveClient(Client client) {
        var url = "jdbc:h2:tcp://localhost/~/test";

        try {
             var con = DriverManager.getConnection(url, "sa", "password");
             var stm = con.createStatement();
             stm.execute("INSERT INTO TEST VALUES ('" + client.getID() + "')");

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
