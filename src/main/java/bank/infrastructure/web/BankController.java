package bank.infrastructure.web;

import bank.application.BankService;
import bank.application.exceptions.ClientNotFoundException;
import bank.application.exceptions.IllegalClientIdException;
import bank.domain.Client;
import bank.application.exceptions.RepositoryError;
import bank.infrastructure.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/bank/v2/clients/{clientId}/transactions/")
    public ResponseEntity<Void> changeBalanceV2 (@PathVariable("clientId") UUID clientId, @RequestBody MoneyDtoV2 transaction) {
        if (clientId == null) {
            throw new IllegalClientIdException("INCORRECT ID");
        }
        bankService.changeBalance(clientId, transaction.getCurrency(), transaction.getAmount());

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/bank/v2/clients/{clientId}")
    public ClientDtoV2 getBalance(@PathVariable("clientId") String uuidStr) {
        Client client = bankService.getClientById(getUuid(uuidStr));
        if (client == null) {
            throw new ClientNotFoundException("CLIENT NOT FOUND");
        }
        return ClientDtoV2.toDto(client);
    }
}
