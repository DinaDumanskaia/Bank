package bank;

import java.util.Objects;

public class ClientWithDTO {
    private String id;
    private int balance;
    //private List<Transaction> transactions;

    public ClientWithDTO(String id, int balance) {
        this.id = id;
        this.balance = Objects.requireNonNull(balance);
    }

    public String getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public void setId(String id, int balance) {
        this.id = id;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
