package web;

import bank.BankService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class Controller {
    private final ArrayList<String> uuids = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    BankService bankService;

    @PostMapping("/bank/v1/clients/")
    public ResponseEntity<ClientDTO> createClient() throws Exception {
        ClientDTO cratedClient = bankService.createNewClient();
        uuids.add(cratedClient.getId());
        return new ResponseEntity<>(cratedClient, HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}")
    public ResponseEntity<String> isClientExists(@PathVariable("clientId") String clientId) {
        if (uuids.contains(clientId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/bank/v1/clients/{clientId}/balance")
    public ResponseEntity<String> getClientBalance(@PathVariable("clientId") String clientId) {
        if (uuids.contains(clientId)) {
            int balance = bankService.getBalance(clientId);
            return new ResponseEntity<>(String.valueOf(balance), HttpStatus.FOUND);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/bank/v1/clients/{clientId}/balance/")
    public ResponseEntity<String> changeBalance(@PathVariable("clientId") String clientId, @RequestBody String transactionArgument) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(transactionArgument);
        int transaction = jsonNode.get("balance").asInt();

        if (uuids.contains(clientId)) {
            int balance = bankService.getBalance(clientId);
            if (balance + transaction < 0) {
                return ResponseEntity.badRequest().build();
            } else {
                bankService.changeBalance(clientId, transaction);
                return ResponseEntity.ok().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
