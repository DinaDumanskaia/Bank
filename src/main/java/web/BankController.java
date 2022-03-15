package web;

import bank.BankService;
import bank.Client;
import bank.TransactionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class BankController {

    @Autowired
    BankService bankService;

    @PostMapping("/bank/v1/clients/")
    public ResponseEntity<ClientDTO> createClient() {
        Client client = bankService.createNewClient();
        return new ResponseEntity<>(ClientDTO.toDto(client), HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}")
    public ClientDTO getClient(@PathVariable("clientId") UUID clientId) {
        Client client = bankService.getClientById(clientId);
        return ClientDTO.toDto(client);
    }

    @PostMapping("/bank/v1/clients/{clientId}/transactions/")
    public ResponseEntity<Void> changeBalance(@PathVariable("clientId") UUID clientId, @RequestBody MoneyDto transaction) throws Exception {
        bankService.changeBalance(clientId, transaction.getAmount());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}/transactions/")
    public List<TransactionDTO> getListOfTransactions(@PathVariable("clientId") UUID clientId) {
        return bankService.getTransactions(clientId)
                .stream().map(TransactionDTO::toDto)
                .collect(Collectors.toList());
    }

}
