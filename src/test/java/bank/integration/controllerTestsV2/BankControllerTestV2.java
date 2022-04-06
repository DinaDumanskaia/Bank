package bank.integration.controllerTestsV2;

import bank.application.BankService;
import bank.domain.Client;
import bank.domain.Currency;
import bank.domain.Transaction;
import bank.infrastructure.web.dto.TransactionDtoV2;
import bank.infrastructure.web.v1.dto.MoneyDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BankControllerTestV2 {
    public static final int TRANSFERRED_MONEY_VALUE = 100;
    @Autowired
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private BankService bankService;

    @Test
    public void testDepositMoneyForCreatedClientV2() throws Exception {
        Client client = new Client();

        ResultMatcher matcher = (result) ->
                AssertionErrors.assertEquals("Status", HttpStatus.CREATED.value(),
                        result.getResponse().getStatus());
        String urlTemplate = "/bank/v2/clients/" + client.getID().toString() + "/transactions/";
        //String moneyDto = objectMapper.writeValueAsString(moneyDTO);
        String moneyDto = "{\"amount\":\"" + TRANSFERRED_MONEY_VALUE + "\", \"currency\":\"EUR\"}";

        mvc.perform(
                post(urlTemplate)
                        .content(moneyDto)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(matcher);

        Mockito.verify(bankService).changeBalance(client.getID(), Currency.EUR, TRANSFERRED_MONEY_VALUE);
    }

    @Test
    public void getEURTransactions() throws Exception {
        Client client = new Client();
        Date date = new Date();

        client.changeBalance(123, Currency.EUR, date);

        ResultMatcher matcher = (result) ->
                AssertionErrors.assertEquals("Status", HttpStatus.CREATED.value(),
                        result.getResponse().getStatus());

        String moneyDto = "{\"amount\":\"" + TRANSFERRED_MONEY_VALUE + "\", \"currency\":\"EUR\"}";

        mvc.perform(
                post("/bank/v2/clients/" + client.getID().toString() + "/transactions/")
                        .content(moneyDto)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(matcher);

        List<Transaction> listOfTransactions = bankService.getTransactions(client.getID(), Currency.EUR);
        System.out.println(listOfTransactions);

//        String expected = "{\"amount\":\"" + TRANSFERRED_MONEY_VALUE + "\", \"date\":\"" + date + "\"}";
//
//        MvcResult getTransactions = mvc.perform(get("/bank/v1/clients/" + client.getID().toString() + "/transactions/"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(expected))
//                .andReturn();
//



        Mockito.when((bankService).getTransactions(client.getID(), Currency.EUR).size()).thenReturn(1);
    }

    @Test
    public void whenChangeEURBalance_shouldGetEURAccountBalance() throws Exception {
        Client client = new Client();

        client.changeBalance(123, Currency.EUR, new Date());
        Mockito.doReturn(client).when(bankService).getClientById(client.getID());

        String expected = "{\"id\":\"" + client.getID() + "\", \"accounts\":{\"EUR\":123}}";

        mvc.perform(get("/bank/v2/clients/" + client.getID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andReturn();
    }

    private void postTransaction(int money, String clientId) throws Exception {
        postTransaction(money, clientId, HttpStatus.CREATED);
    }

    private void postTransaction(int money, String clientId, HttpStatus status) throws Exception {
        ResultMatcher matcher = (result) ->
                AssertionErrors.assertEquals("Status", status.value(),
                        result.getResponse().getStatus());
        mvc.perform(
                post(transactionsUrl(clientId))
                        .content(getMoneyDto(money))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(matcher);
    }

    private String transactionsUrl(String clientId) {
        return "/bank/v2/clients/" + clientId + "/transactions/";
    }

    private String getMoneyDto(int transaction) throws JsonProcessingException {
        final MoneyDto moneyDTO = new MoneyDto();
        moneyDTO.setAmount(transaction);
        return objectMapper.writeValueAsString(moneyDTO);
    }
}