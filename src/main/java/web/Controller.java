package web;

import bank.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("/bankService/v1/clients/")
public class Controller {
    private BankService bankService;

    @Autowired
    public Controller(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/client_exists")
    public String isExists(@RequestParam String id) {
        if (bankService.clientExists(id)) {
            return "Client with id = " + id + " found!";
        } else {
            return "Client not found";
        }
    }

    @PostMapping(path = "/client_exists555")
    //consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE*/
    public ResponseEntity<String> isExists1(@RequestBody String id) {
        if (bankService.clientExists(id)) {
            return new ResponseEntity<>("Client with id = " + id + " found!", HttpStatus.CREATED);
        } else {
            throw new IllegalArgumentException("Client not found");
        }
    }

    @GetMapping("/create_client")
    public String createdClient(@RequestParam String id) {
        bankService.createNewClient(id);
        return "New client with id =" + id + " has been successfully created";
    }

    @GetMapping("/get_balance")
    public String clientMoneyBalance(@RequestParam Map<String, String> customQuery) {
        Currency currency = Currency.valueOf(customQuery.get("currency").toUpperCase());
        String id = customQuery.get("id");
        try {
            return String.valueOf(bankService.getBalance(id, currency));
        } catch (Throwable t) {
            return "Balance couldn't be returned for the client";
        }
    }
}
