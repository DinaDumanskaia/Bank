package web;

import bank.Client;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ClientDTO {
    private UUID id;
    private int balance;

    @JsonCreator
    public ClientDTO(
            @JsonProperty("id") UUID id,
            @JsonProperty("balance") int balance) {
        this.id = id;
        this.balance = balance;
    }

    static ClientDTO toDto(Client client) {
        return new ClientDTO(client.getID(), client.getBalance());
    }

    public UUID getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

}
