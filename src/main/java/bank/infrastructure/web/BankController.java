package bank.infrastructure.web;

import bank.application.BankService;
import bank.application.exceptions.ClientNotFoundException;
import bank.application.exceptions.IllegalClientIdException;
import bank.domain.Client;
import bank.application.exceptions.RepositoryError;
import bank.domain.Currency;
import bank.domain.MoneyAccount;
import bank.domain.Transaction;
import bank.infrastructure.web.v1.ClientDto;
import bank.infrastructure.web.v1.MoneyDto;
import bank.infrastructure.web.v2.ClientDtoV2;
import bank.infrastructure.web.v2.MoneyDtoV2;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class BankController {

    @Autowired
    BankService bankService;

    @PostMapping("/bank/v1/clients/")
    public ResponseEntity<ClientDto> createClient() throws RepositoryError {
        Client client = bankService.createNewClient();
        return new ResponseEntity<>(ClientDto.toDto(client), HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}")
    public ClientDto getClient(@PathVariable("clientId") String uuidStr) {
        Client client = bankService.getClientById(getUuid(uuidStr));
        if (client == null) {
            throw new ClientNotFoundException("CLIENT NOT FOUND");
        }
        return ClientDto.toDto(client);
    }

    private UUID getUuid(String uuidStr) {
        if (uuidStr == null) {
            throw new IllegalClientIdException("Client ID should not be empty");
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException iae) {
            throw new IllegalClientIdException("Could not parse");
        }
    }

    @PostMapping("/bank/v1/clients/{clientId}/transactions/")
    public ResponseEntity<Void> changeBalance(@PathVariable("clientId") UUID clientId, @RequestBody MoneyDto transaction) {
        if (clientId == null) {
            throw new IllegalClientIdException("INCORRECT ID");
        }
        bankService.changeBalance(clientId, transaction.getAmount());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}/transactions/")
    public List<TransactionDto> getListOfTransactions(@PathVariable("clientId") UUID clientId) {
        if (clientId == null) {
            throw new IllegalClientIdException("INCORRECT ID");
        }
        return bankService.getTransactions(clientId)
                .stream().map(TransactionDto::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/bank/v2/clients/{clientId}")
    public ClientDtoV2 getClientV2(@PathVariable("clientId") String uuidStr) throws JsonProcessingException {
        Client client = bankService.getClientById(getUuid(uuidStr));
        if (client == null) {
            throw new ClientNotFoundException("CLIENT NOT FOUND");
        }
        return ClientDtoV2.toDto(client);
    }

    @PostMapping("/bank/v2/clients/{clientId}/transactions/")
    public ResponseEntity<Void> changeBalanceV2 (@PathVariable("clientId") UUID clientId, @RequestBody MoneyDtoV2 transaction) {
        if (clientId == null) {
            throw new IllegalClientIdException("INCORRECT ID");
        }

        Currency currency = transaction.getCurrency();
        int amount = transaction.getAmount();
        bankService.changeBalance(clientId, transaction.getCurrency(), transaction.getAmount());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/bank/v2/clients/{clientId}/transactions/")
    public Map<String, List<TransactionDto>> getListOfTransactionsV2(@PathVariable("clientId") UUID clientId) {
        if (clientId == null) {
            throw new IllegalClientIdException("INCORRECT ID");
        }
        Client client = bankService.getClientById(clientId);
        Map<Currency, MoneyAccount> map = client.getMoneyAccounts();
        Map<String, List<TransactionDto>> map1 = new HashMap<>();
        for (Currency currency : map.keySet()) {
            List<Transaction> list = map.get(currency).getMoneyAccountTransactionList();
            List<TransactionDto> transactionDtos = list.stream().map(TransactionDto::toDto)
                    .collect(Collectors.toList());

            map1.put(currency.name(), transactionDtos);
        }
        return map1;
    }
}
