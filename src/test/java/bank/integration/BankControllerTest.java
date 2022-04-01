package bank.integration;

import bank.application.BankService;
import bank.domain.Client;
import bank.domain.Currency;
import bank.domain.MoneyAccount;
import bank.domain.Transaction;
import bank.infrastructure.web.dto.ClientDto;
import bank.infrastructure.web.dto.MoneyDto;
import bank.infrastructure.web.dto.TransactionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BankControllerTest {
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
        MoneyAccount moneyAccount = new MoneyAccount(clientID, Collections.emptyList());
        Map<Currency, MoneyAccount> map = new HashMap<>();
        map.put(Currency.RUB, moneyAccount);
        return new Client(clientID, map);
    }

    //@Test
    public void checkBalanceOfCreatedClientIsZero() throws Exception {
        MvcResult mvcResult = createClient();
        MvcResult getClientResult = mvc.perform(get("/bank/v1/clients/" + clientId(mvcResult)))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(0, getClientDTO(getClientResult).getBalance());
    }

    private MvcResult createClient() throws Exception {
        return mvc.perform(post("/bank/v1/clients/"))
                .andExpect(status().isCreated())
                .andReturn();
    }

    //@Test
    public void headClientThatIsNotExist() throws Exception {
        mvc.perform(head("/bank/v1/clients/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    //@Test
    public void testDepositMoneyForCreatedClient() throws Exception {
        MvcResult result = createClient();

        postTransaction(100, clientId(result));
    }

    private void postTransaction(int money, String clientId) throws Exception {
        mvc.perform(
                post(transactionsUrl(clientId))
                        .content(getMoneyDto(money))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    private String transactionsUrl(String clientId) {
        return "/bank/v1/clients/" + clientId + "/transactions/";
    }

    //@Test
    public void getBalanceOfCreatedClient() throws Exception {
        MvcResult result = createClient();
        mvc.perform(get("/bank/v1/clients/" + clientId(result) + "/balance/")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    //@Test
    public void testDepositNegative() throws Exception {
        MvcResult result = createClient();

        mvc.perform(post(transactionsUrl(clientId(result)))
            .content(getMoneyDto(-100))
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
        }

    //@Test
    public void getTransactions() throws Exception {
        MvcResult mvcResult = createClient();
        postTransaction(10, clientId(mvcResult));
        postTransaction(400, clientId(mvcResult));
        MvcResult getClientResult = mvc.perform(get(transactionsUrl(clientId(mvcResult))))
                .andExpect(status().isOk())
                .andReturn();

        List<TransactionDto> transactionsDTO = getTransactionsDTO(getClientResult);
        Assertions.assertEquals(10, transactionsDTO.get(0).getAmount());
        Assertions.assertEquals(400, transactionsDTO.get(1).getAmount());
    }

    //@Test
    public void getTransactionDate() throws Exception {
        MvcResult mvcResult = createClient();

        long start = System.currentTimeMillis();
        Date date = getTransactionDate(mvcResult);
        long finish = System.currentTimeMillis();

        assertTrue(start < date.getTime());
        assertTrue(finish > date.getTime());
    }

    private Date getTransactionDate(MvcResult mvcResult) throws Exception {
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        postTransaction(10, clientId(mvcResult));

        MvcResult getClientResult = mvc.perform(get(transactionsUrl(clientId(mvcResult))))
                .andExpect(status().isOk())
                .andReturn();

        List<TransactionDto> transactionsDTO = getTransactionsDTO(getClientResult);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zz yyyy", Locale.US);
        return simpleDateFormat.parse(transactionsDTO.get(0).getDate());
    }

    private List<TransactionDto> getTransactionsDTO(MvcResult getClientResult) throws UnsupportedEncodingException, JsonProcessingException {
        final String json = getClientResult.getResponse().getContentAsString();
        return objectMapper.readValue(json, new TypeReference<>(){});
    }

    private String getMoneyDto(int transaction) throws JsonProcessingException {
        final MoneyDto moneyDTO = new MoneyDto();
        moneyDTO.setAmount(transaction);
        return objectMapper.writeValueAsString(moneyDTO);
    }

    private String clientId(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        return getClientDTO(result).getId().toString();
    }

    private ClientDto getClientDTO(MvcResult response) throws UnsupportedEncodingException, JsonProcessingException {
        final String json = response.getResponse().getContentAsString();
        return objectMapper.readValue(json, ClientDto.class);
    }

}