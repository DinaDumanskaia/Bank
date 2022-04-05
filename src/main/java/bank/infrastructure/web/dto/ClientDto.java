package bank.infrastructure.web.dto;

import bank.application.exceptions.ClientNotFoundException;
import bank.domain.Client;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class ClientDto {
    private UUID id;
    private Integer balance;

    @JsonCreator
    public ClientDto(
            @JsonProperty("id") UUID id,
            @JsonProperty("balance") Integer balance) {
        this.id = id;
        this.balance = balance;
    }

    public static ClientDto toDto(Client client) {
        if (client == null) {
            throw new ClientNotFoundException("CLIENT NOT FOUND");
        }
        return new ClientDto(client.getID(), client.getBalance());
    }

    public UUID getId() {
        return id;
    }

    public Integer getBalance() {
        return balance;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

}
