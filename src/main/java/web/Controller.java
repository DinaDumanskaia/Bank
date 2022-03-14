package web;

import bank.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
public class Controller {

    @Autowired
    BankService bankService;

    @PostMapping("/bank/v1/clients/")
    public ResponseEntity<ClientDTO> createClient() {
        UUID id = UUID.randomUUID();
        bankService.createNewClient(id.toString());
        return new ResponseEntity<>(new ClientDTO(id.toString(), 0, Collections.emptyList()), HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}")
    public ResponseEntity<String> isClientExists(@PathVariable("clientId") String clientId) {
        if (bankService.clientExists(clientId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/bank/v1/clients/{clientId}/balance")
    public ResponseEntity<Integer> getClientBalance(@PathVariable("clientId") String clientId) {
        return new ResponseEntity<>(bankService.getBalance(clientId), HttpStatus.OK);
    }

    @PostMapping("/bank/v1/clients/{clientId}/transaction/")
    public ResponseEntity<Integer> changeBalance(@PathVariable("clientId") String clientId, @RequestBody TransactionDto transaction) throws Exception {
        bankService.changeBalance(clientId, transaction.getBalance());

        int i = bankService.getBalance(clientId);
        return new ResponseEntity<>(i, HttpStatus.OK);
    }

}
