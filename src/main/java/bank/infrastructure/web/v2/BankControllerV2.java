package bank.infrastructure.web.v2;

import bank.application.BankService;
import bank.application.exceptions.ClientNotFoundException;
import bank.application.exceptions.IllegalClientIdException;
import bank.domain.Client;
import bank.infrastructure.web.v1.dto.TransactionDto;
import bank.infrastructure.web.v2.dto.ClientDtoV2;
import bank.infrastructure.web.v2.dto.MoneyDtoV2;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class BankControllerV2 {

    @Autowired
    BankService bankService;

    @PostMapping("/bank/v2/clients/{clientId}/transactions/")
    public ResponseEntity<Void> changeBalanceV2(@PathVariable("clientId") UUID clientId, @RequestBody MoneyDtoV2 transaction) {
        if (clientId == null) {
            throw new IllegalClientIdException("INCORRECT ID");
        }
        bankService.changeBalance(clientId, transaction.getCurrency(), transaction.getAmount());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/bank/v2/clients/{clientId}")
    public ClientDtoV2 getClientV2(@PathVariable("clientId") String uuidStr) throws JsonProcessingException {
        Client client = bankService.getClientById(getUuid(uuidStr));
        if (client == null) {
            throw new ClientNotFoundException("CLIENT NOT FOUND");
        }
        return ClientDtoV2.toDto(client);
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

    @GetMapping("/bank/v2/clients/{clientId}/transactions/")
    public List<TransactionDto> getListOfTransactionsV2(@PathVariable("clientId") UUID clientId) {
        if (clientId == null) {
            throw new IllegalClientIdException("INCORRECT ID");
        }
        return bankService.getTransactions(clientId)
                .stream().map(TransactionDto::toDto)
                .collect(Collectors.toList());
    }
}