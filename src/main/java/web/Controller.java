package web;

import bank.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/bank/v1")
public class Controller {
    private static BankService bankService;
    private static UUID uuid;

    @Autowired
    public Controller(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/clients")
    List<Client> all() {
        return bankService.findAll();
    }

    @PostMapping("/clients")
    public static UUID createClient() throws Exception {
        return UUID.randomUUID();
    }

//    private static void createBalance(UUID uuid) throws Exception {
//        bankService.changeBalance(uuid.toString(), 0);
//    }

    @GetMapping("/clients/{id}/balance")
    int getClientBalance(@PathVariable Long id) {
        if (bankService.clientExists(id.toString())) {
            return bankService.getBalance(id.toString());
        } else {
            throw new IllegalArgumentException();
        }
    }


//    @GetMapping("/get_balance")
//    public String clientMoneyBalance(@RequestParam Map<String, String> customQuery) {
//        Currency currency = Currency.valueOf(customQuery.get("currency").toUpperCase());
//        String id = customQuery.get("id");
//        try {
//            return String.valueOf(bankService.getBalance(id, currency));
//        } catch (Throwable t) {
//            return "Balance couldn't be returned for the client";
//        }
//    }

//    @PostMapping(path = "/client_exists555")
//    consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE*/
//    public ResponseEntity<String> isExists1(@RequestBody String id) {
//        if (bankService.clientExists(id)) {
//            return new ResponseEntity<>("Client with id = " + id + " found!", HttpStatus.CREATED);
//        } else {
//            throw new IllegalArgumentException("Client not found");
//        }
//    }
}
