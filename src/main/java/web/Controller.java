package web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.UUID;

@RestController
public class Controller {
    ArrayList<String> uuids = new ArrayList<>();


    @PostMapping("/bank/v1/clients/")
    public ResponseEntity<String> createClient() {
        final String s = String.valueOf(UUID.randomUUID());
        uuids.add(s);
        return new ResponseEntity<>(s, HttpStatus.CREATED);
    }

    @GetMapping("/bank/v1/clients/{clientId}")
    public ResponseEntity<String> getClient(@PathVariable("clientId") String clientId) {
        if (uuids.contains(clientId)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

}
