package web;

import bank.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Controller {
    private BankService bs;
    private DateProvider dateProvider;

{
    dateProvider = new DateProviderImpl();
    bs = new BankService(dateProvider);
    //bs.createNewClient("7");
}
    @GetMapping("/client_exists")
    public String isExists(@RequestParam String id) {
        if (bs.clientExists(id)) {
            return "Client with id = " + id + " found!";
        } else {
            return "Client not found";
        }
    }

    @GetMapping("/create_client")
    public String createdClient(@RequestParam String id) {
        bs.createNewClient(id);
        return "New client with id =" + id + " has been successfully created";
    }

    @GetMapping("/get_balance")
    public String clientMoneyBalance(@RequestParam Map<String, String> customQuery) {
        Currency currency = Currency.valueOf(customQuery.get("currency").toUpperCase());
        String id = customQuery.get("id");
        try {
            return String.valueOf(bs.getBalance(id, currency));
        } catch (Throwable t) {
            return "Balance couldn't be returned for the client";
        }
    }
}
