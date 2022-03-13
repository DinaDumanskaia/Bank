package web;

import bank.TransactionData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ClientDTO {
    private String id;
    private int balance;
    private List<TransactionData> transactions;

    @JsonCreator
    public ClientDTO(
            @JsonProperty("id") String id,
            @JsonProperty("balance") int balance,
            @JsonProperty("transactions") List<TransactionData> transactions) {
        this.id = id;
        this.balance = balance;
        this.transactions = transactions;
    }

    public String getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public List<TransactionData> getTransactions() {
        return transactions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setTransactions(List<TransactionData> transactions) {
        this.transactions = transactions;
    }
}
