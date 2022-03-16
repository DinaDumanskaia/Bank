package DataBase;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaSeH2Memory {
    public static void main(String[] args) {

        var url = "jdbc:h2:tcp://localhost/~/test";

        try (var con = DriverManager.getConnection(url, "sa", "password");
             var stm = con.createStatement();
             var rs = stm.executeQuery("SELECT * FROM TEST")) {

            if (rs.next()) {

                System.out.println(rs.getInt(1));
            }

        } catch (SQLException ex) {

            var lgr = Logger.getLogger(JavaSeH2Memory.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
