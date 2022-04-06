package bank.integration;

import bank.application.BankService;
import bank.domain.Client;
import bank.domain.Currency;
import bank.domain.MoneyAccount;
import bank.domain.NegativeBalanceException;
import bank.infrastructure.web.v1.ClientDto;
import bank.infrastructure.web.v1.MoneyDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BankControllerTest {
    public static final int TRANSFERRED_MONEY_VALUE = 100;
    @Autowired
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private BankService bankService;

    @Test
    public void checkClientIsCreated() throws Exception {
        Client client = createMockClient();

        Mockito.when(bankService.createNewClient()).thenReturn(client);

        String expected = objectMapper.writeValueAsString(ClientDto.toDto(client));
        mvc.perform(post("/bank/v1/clients/"))
                .andExpect(status().isCreated())
                .andExpect(content().json(expected));
    }

    private Client createMockClient() {
        UUID clientID = UUID.randomUUID();
        MoneyAccount moneyAccount = new MoneyAccount(clientID, new ArrayList<>());
        Map<Currency, MoneyAccount> map = new HashMap<>();
        map.put(Currency.RUB, moneyAccount);
        return new Client(clientID, map);
    }

    @Test
    public void checkBalanceOfCreatedClientIsZero() throws Exception {
        Client client = createMockClient();

        Mockito.when(bankService.getClientById(any()))
                .thenAnswer(invocation -> invocation.getArgument(0).equals(client.getID()) ? client : null);

        String expected = objectMapper.writeValueAsString(ClientDto.toDto(client));

        MvcResult getClientResult = mvc.perform(get("/bank/v1/clients/" + client.getID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andReturn();

        ClientDto clientDto = getClientDTO(getClientResult);
        Assertions.assertNotNull(clientDto);
        assertEquals(0, clientDto.getBalance());
    }

    @Test
    public void getClientThatIsNotExist() throws Exception {
        Client client = createMockClient();

        Mockito.when(bankService.getClientById(any()))
                .thenAnswer(invocation -> invocation.getArgument(0).equals(client.getID()) ? client : null);

        mvc.perform(get("/bank/v1/clients/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDepositMoneyForCreatedClient() throws Exception {
        Client client = createMockClient();

        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            Integer value = invocation.getArgument(1);

            assertEquals(client.getID(), id);
            client.changeBalance(value, Currency.RUB, new Date());
            return null;
        }).when(bankService).changeBalance(any(UUID.class), any(Integer.class));

        postTransaction(TRANSFERRED_MONEY_VALUE, client.getID().toString());
        Assertions.assertEquals(TRANSFERRED_MONEY_VALUE, client.getBalance());
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
        return "/bank/v1/clients/" + clientId + "/transactions/";
    }

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
    public void checkOverWithdraw() throws Exception {
        Client client = createMockClient();

        Mockito.doThrow(new NegativeBalanceException("NOT ENOUGH MONEY"))
                .when(bankService).changeBalance(any(UUID.class), any(Integer.class));

        postTransaction(TRANSFERRED_MONEY_VALUE * -1, client.getID().toString(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getTransactions() throws Exception {
        Client client = createMockClient();

        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            Integer value = invocation.getArgument(1);

            assertEquals(client.getID(), id);
            client.changeBalance(value, Currency.RUB, new Date());
            return null;
        }).when(bankService).changeBalance(any(UUID.class), any(Integer.class));

        postTransaction(TRANSFERRED_MONEY_VALUE, client.getID().toString());
        postTransaction((TRANSFERRED_MONEY_VALUE * 2), client.getID().toString());

        assertEquals(TRANSFERRED_MONEY_VALUE, client.getListOfTransactions().get(0).getAmount());
        assertEquals((TRANSFERRED_MONEY_VALUE * 2), client.getListOfTransactions().get(1).getAmount());
    }

    @Test
    public void getTransactionDate() throws Exception {
        Client client = createMockClient();

        Date date = new Date(123);

        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            Integer value = invocation.getArgument(1);

            assertEquals(client.getID(), id);
            client.changeBalance(value, Currency.RUB, date);
            return null;
        }).when(bankService).changeBalance(any(UUID.class), any(Integer.class));

        postTransaction(TRANSFERRED_MONEY_VALUE, client.getID().toString());
        postTransaction((TRANSFERRED_MONEY_VALUE * 2), client.getID().toString());

        Assertions.assertEquals(date.getTime(), client.getListOfTransactions().get(0).getDate().getTime());
        Assertions.assertEquals(date.getTime(), client.getListOfTransactions().get(1).getDate().getTime());
    }

    private String getMoneyDto(int transaction) throws JsonProcessingException {
        final MoneyDto moneyDTO = new MoneyDto();
        moneyDTO.setAmount(transaction);
        return objectMapper.writeValueAsString(moneyDTO);
    }

    private ClientDto getClientDTO(MvcResult response) throws UnsupportedEncodingException, JsonProcessingException {
        final String json = response.getResponse().getContentAsString();
        return objectMapper.readValue(json, ClientDto.class);
    }

    @Test
    public void checkBalanceOfCreatedClientIsZeroV2() throws Exception {
        Client client = new Client();

        client.changeBalance(123, Currency.EUR, new Date());
        Mockito.doReturn(client).when(bankService).getClientById(client.getID());

        String expected = "{\"id\":\"" + client.getID() + "\", \"accounts\":{\"EUR\":123}}";

        mvc.perform(get("/bank/v2/clients/" + client.getID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andReturn();
    }

}