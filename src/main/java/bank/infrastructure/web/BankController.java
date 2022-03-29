package bank.infrastructure.web;

import bank.application.BankService;
import bank.application.IllegalClientIdException;
import bank.domain.Client;
import bank.application.RepositoryError;
import bank.infrastructure.web.dto.ClientDto;
import bank.infrastructure.web.dto.MoneyDto;
import bank.infrastructure.web.dto.TransactionDto;
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

    @PostMapping("/bank/v1/clients/{clientId}/transactions/")
    public ResponseEntity<Void> changeBalance(@PathVariable("clientId") UUID clientId, @RequestBody MoneyDto transaction) throws IllegalClientIdException {
        bankService.changeBalance(clientId, transaction.getAmount());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}")
    public ClientDto getClient(@PathVariable("clientId") String uuidStr) throws IllegalClientIdException {
        Client client = bankService.getClientById(getUuid(uuidStr));
        return ClientDto.toDto(client);
    }

    private UUID getUuid(String uuidStr) throws IllegalClientIdException {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException iae) {
            throw new IllegalClientIdException("Could not parse");
        }
    }

    @GetMapping("/bank/v1/clients/{clientId}/transactions/")
    public List<TransactionDto> getListOfTransactions(@PathVariable("clientId") UUID clientId) throws IllegalClientIdException {
        return bankService.getTransactions(clientId)
                .stream().map(TransactionDto::toDto)
                .collect(Collectors.toList());
    }

}
