package bank.infrastructure.web;

import bank.application.BankService;
import bank.domain.Client;
import bank.application.RepositoryError;
import bank.infrastructure.web.dto.ClientDto;
import bank.infrastructure.web.dto.MoneyDto;
import bank.infrastructure.web.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//@RestController
public class BankController {

    @Autowired
    BankService bankService;

    @PostMapping("/bank/v1/clients/")
    public ResponseEntity<ClientDto> createClient(Model model) throws RepositoryError {
        Client client = bankService.createNewClient();
        model.addAttribute("client", client);
        return new ResponseEntity<>(ClientDto.toDto(client), HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}")
    public ClientDto getClient(@PathVariable("clientId") UUID clientId) {
        Client client = bankService.getClientById(clientId);
        return ClientDto.toDto(client);
    }

    @PostMapping("/bank/v1/clients/{clientId}/transactions/")
    public ResponseEntity<Void> changeBalance(@PathVariable("clientId") UUID clientId, @RequestBody MoneyDto transaction) throws Exception {
        bankService.changeBalance(clientId, transaction.getAmount());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}/transactions/")
    public List<TransactionDto> getListOfTransactions(@PathVariable("clientId") UUID clientId) {
        return bankService.getTransactions(clientId)
                .stream().map(TransactionDto::toDto)
                .collect(Collectors.toList());
    }

}
