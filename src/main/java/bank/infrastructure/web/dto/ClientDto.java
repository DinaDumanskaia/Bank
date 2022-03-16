package bank.infrastructure.web.dto;

import bank.domain.Client;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ClientDto {
    private UUID id;
    private int balance;

    @JsonCreator
    public ClientDto(
            @JsonProperty("id") UUID id,
            @JsonProperty("balance") int balance) {
        this.id = id;
        this.balance = balance;
    }

    public static ClientDto toDto(Client client) {
        return new ClientDto(client.getID(), client.getBalance());
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
