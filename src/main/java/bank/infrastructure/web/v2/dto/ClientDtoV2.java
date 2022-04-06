package bank.infrastructure.web.v2.dto;

import bank.application.exceptions.ClientNotFoundException;
import bank.domain.Client;
import bank.domain.Currency;
import bank.domain.MoneyAccount;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientDtoV2 {
    private UUID id;
    private Map<String, Integer> accounts;


    @JsonCreator
    public ClientDtoV2(
            @JsonProperty("id") UUID id,
            @JsonProperty("accounts") Map<String, Integer> accounts) {

        this.id = id;
        this.accounts = accounts;
    }

    public static ClientDtoV2 toDto(Client client) {
        if (client == null) {
            throw new ClientNotFoundException("CLIENT NOT FOUND");
        }
        return new ClientDtoV2(client.getID(), getBalances(client));
    }

    private static Map<String, Integer> getBalances(Client client) {
        Map<String, Integer> balances = new HashMap<>();
        Map<Currency, MoneyAccount> moneyAccounts = client.getMoneyAccounts();
        for (Currency currency : moneyAccounts.keySet()) {
            balances.put(currency.name(), moneyAccounts.get(currency).getBalance());
        }
        return balances;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, Integer> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, Integer> accounts) {
        this.accounts = accounts;
    }
}
