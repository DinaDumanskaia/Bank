package web;

import bank.Client;
import bank.TransactionData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class ClientDTO {
    private UUID id;
    private int balance;
    private List<TransactionData> transactions;

    @JsonCreator
    public ClientDTO(
            @JsonProperty("id") UUID id,
            @JsonProperty("balance") int balance,
            @JsonProperty("transactions") List<TransactionData> transactions) {
        this.id = id;
        this.balance = balance;
        this.transactions = transactions;
    }

    static ClientDTO toDto(Client client) {
        return new ClientDTO(client.getID(), client.getBalance(), client.getListOfTransactions());
    }

    public UUID getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public List<TransactionData> getTransactions() {
        return transactions;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setTransactions(List<TransactionData> transactions) {
        this.transactions = transactions;
    }
}
