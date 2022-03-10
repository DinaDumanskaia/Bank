package web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class Controller {

    @PostMapping("/bank/v1/clients/")
    public static ResponseEntity<String> createClient() {
        return new ResponseEntity<>(String.valueOf(UUID.randomUUID()), HttpStatus.CREATED);
    }

}
