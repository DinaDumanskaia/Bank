package web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BankControllerTest {
    @Autowired
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void postClient() throws Exception {
        mvc.perform(post("/bank/v1/clients/"))
                .andExpect(status().isCreated());
    }

    @Test
    public void headClient() throws Exception {
        MvcResult resultActions = createClient();

        mvc.perform(head("/bank/v1/clients/" + clientId(resultActions)))
                .andExpect(status().isOk());
    }

    @Test
    public void getClient() throws Exception {
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

    @Test
    public void headClientThatIsNotExist() throws Exception {
        mvc.perform(head("/bank/v1/clients/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDepositMoneyForCreatedClient() throws Exception {
        MvcResult mvcResult = createClient();

        mvc.perform(post("/bank/v1/clients/" + clientId(mvcResult) + "/transaction/")
                .content(getTransactionDto(100))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getBalanceOfCreatedClient() throws Exception {
        MvcResult result = createClient();
        mvc.perform(get("/bank/v1/clients/" + clientId(result) + "/balance/")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    @Test
    public void testDepositNegative() throws Exception {
        MvcResult result = createClient();

        mvc.perform(post("/bank/v1/clients/" + clientId(result) + "/transaction/")
            .content(getTransactionDto(-100))
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        }

    private String getTransactionDto(int transaction) throws JsonProcessingException {
        final TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBalance(transaction);
        return objectMapper.writeValueAsString(transactionDto);
    }

    private String clientId(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        return getClientDTO(result).getId().toString();
    }

    private ClientDTO getClientDTO(MvcResult response) throws UnsupportedEncodingException, JsonProcessingException {
        final String json = response.getResponse().getContentAsString();
        return objectMapper.readValue(json, ClientDTO.class);
    }

}