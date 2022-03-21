package bank.infrastructure.web.Thymeleaf;

import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.springframework.boot.autoconfigure.domain.EntityScan;

public class ClientForThymeleaf {
    String id;
    String balance;

    public ClientForThymeleaf() {}

    public ClientForThymeleaf(String id, String balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
