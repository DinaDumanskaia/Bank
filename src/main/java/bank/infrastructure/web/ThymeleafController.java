package bank.infrastructure.web;

import bank.application.BankService;
import bank.application.RepositoryError;
import bank.domain.Client;
import bank.infrastructure.web.dto.ClientDto;
import bank.infrastructure.web.dto.MoneyDto;
import bank.infrastructure.web.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class ThymeleafController {

    @Autowired
    BankService bankService;

    @PostMapping("/bank/v1/clients/")
    public String createClient(Model model) throws RepositoryError {
        Client client = bankService.createNewClient();
        model.addAttribute("id", client.getID());
        model.addAttribute("balance", client.getBalance());
        //ClientDto.toDto(client);
        return "client";
    }

    @GetMapping("/bank/v1/clients/{clientId}/transactions")
    public String changeBalance(@PathVariable("clientId") UUID clientId, Model model) {
        model.getAttribute("transactionAmount");
        return "transaction";
    }

    @GetMapping("/transactionProcessing")
    public String transactionProcessing(@RequestParam UUID id, @RequestParam int transaction, Model model) throws URISyntaxException {
        bankService.changeBalance(id, transaction);
        int currentBalance = bankService.getBalance(id);
        model.addAttribute("balance", currentBalance);
        model.addAttribute("id", id);
        return "transactionProcessingResult";
    }


}
