package bank.infrastructure.web.dto;

import bank.application.exceptions.ClientNotFoundException;
import bank.domain.Client;
import bank.domain.Currency;
import bank.domain.MoneyAccount;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.UUID;

public class ClientDtoV2 {
    private UUID id;
    Map<Currency, MoneyAccount> map;

    @JsonCreator
    public ClientDtoV2(
            @JsonProperty("id") UUID id,
            @JsonProperty("balance") Map<Currency, MoneyAccount> map) {
        this.id = id;
        this.map = map;
    }

    public static ClientDtoV2 toDto(Client client) {
        if (client == null) {
            throw new ClientNotFoundException("CLIENT NOT FOUND");
        }
        return new ClientDtoV2(client.getID(), client.getMoneyAccounts());
    }

    public UUID getId() {
        return id;
    }

    public Integer getMoneyAccountBalance(Currency currency, Map<Currency, MoneyAccount> map) {
        return map.get(currency).getBalance();
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
