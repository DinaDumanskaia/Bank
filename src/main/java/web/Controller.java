package web;

import bank.BankService;
import bank.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class Controller {

    @Autowired
    BankService bankService;

    @PostMapping("/bank/v1/clients/")
    public ResponseEntity<ClientDTO> createClient() {
        Client client = bankService.createNewClient();
        return new ResponseEntity<>(ClientDTO.toDto(client), HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}")
    public ClientDTO getClient(@PathVariable("clientId") UUID clientId) {
        Client client = bankService.getClient(clientId);
        return ClientDTO.toDto(client);
    }

    @GetMapping("/bank/v1/clients/{clientId}/balance")
    public ResponseEntity<Integer> getClientBalance(@PathVariable("clientId") UUID clientId) {
        return new ResponseEntity<>(bankService.getBalance(clientId), HttpStatus.OK);
    }

    @PostMapping("/bank/v1/clients/{clientId}/transaction/")
    public ResponseEntity<Integer> changeBalance(@PathVariable("clientId") UUID clientId, @RequestBody TransactionDto transaction) throws Exception {
        bankService.changeBalance(clientId, transaction.getBalance());

        int i = bankService.getBalance(clientId);
        return new ResponseEntity<>(i, HttpStatus.OK);
    }

}
