package bank.integration;

import bank.application.BankService;
import bank.domain.Client;
import bank.domain.Currency;
import bank.domain.Transaction;
import bank.infrastructure.web.v2.MoneyDtoV2;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

        String moneyDto = "{\"amount\":\"" + TRANSFERRED_MONEY_VALUE + "\", \"currency\":\"EUR\"}";

        mvc.perform(
                post(urlTemplate)
                        .content(moneyDto)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(matcher);

        Mockito.verify(bankService).changeBalance(client.getID(), Currency.EUR, TRANSFERRED_MONEY_VALUE);
    }

//    @Test
    public void getEURTransactions() throws Exception {
        Client client = new Client();

        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            Integer value = invocation.getArgument(1);

            assertEquals(client.getID(), id);
            client.changeBalance(value, Currency.RUB, new Date());
            return null;
        }).when(bankService).changeBalance(any(UUID.class), any(Integer.class));

        postTransaction(TRANSFERRED_MONEY_VALUE, Currency.RUB, client.getID().toString());
        postTransaction((TRANSFERRED_MONEY_VALUE * 2),Currency.RUB, client.getID().toString());

        List<Transaction> listOfTransactions = client.getMoneyAccounts().get(Currency.RUB).getMoneyAccountTransactionList();
        assertEquals(TRANSFERRED_MONEY_VALUE, listOfTransactions.get(0).getAmount());
        assertEquals((TRANSFERRED_MONEY_VALUE * 2), listOfTransactions.get(1).getAmount());
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

    private void postTransaction(int money, Currency currency, String clientId) throws Exception {
        postTransaction(money, currency, clientId, HttpStatus.CREATED);
    }

    private void postTransaction(int money, Currency currency, String clientId, HttpStatus status) throws Exception {
        ResultMatcher matcher = (result) ->
                AssertionErrors.assertEquals("Status", status.value(),
                        result.getResponse().getStatus());
        mvc.perform(
                post(transactionsUrl(clientId))
                        .content(getMoneyDto(money, currency))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(matcher);
    }

    private String transactionsUrl(String clientId) {
        return "/bank/v2/clients/" + clientId + "/transactions/";
    }

    private String getMoneyDto(int transaction, Currency currency) throws JsonProcessingException {
        final MoneyDtoV2 moneyDTO = new MoneyDtoV2();
        moneyDTO.setAmount(transaction);
        moneyDTO.setCurrency(currency);
        return objectMapper.writeValueAsString(moneyDTO);
    }
}