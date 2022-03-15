package web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerTestIT {
    @Autowired
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();
    ResultActions resultActions;

    @Test
    public void postClient() throws Exception {
        mvc.perform(post("/bank/v1/clients/"))
                .andExpect(status().isCreated());
    }

    @Test
    public void headClient() throws Exception {
        resultActions = createClient();
        mvc.perform(head("/bank/v1/clients/" + clientId(resultActions)))
                .andExpect(status().isOk());
    }

    private ResultActions createClient() throws Exception {
        resultActions = mvc.perform(post("/bank/v1/clients/"))
                .andExpect(status().isCreated());
        return resultActions;
    }

    @Test
    public void headClientThatIsNotExist() throws Exception {
        mvc.perform(head("/bank/v1/clients/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDepositMoneyForCreatedClient() throws Exception {
        resultActions = createClient();

        mvc.perform(post("/bank/v1/clients/" + clientId(resultActions) + "/transaction/")
                .content(getTransactionDto(100))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getBalanceOfCreatedClient() throws Exception {
        resultActions = createClient();
        mvc.perform(get("/bank/v1/clients/" + clientId(resultActions) + "/balance/")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    @Test
    public void testDepositNegative() throws Exception {
    resultActions = createClient();

        mvc.perform(post("/bank/v1/clients/" + clientId(resultActions) + "/transaction/")
            .content(getTransactionDto(-100))
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        }

    private String getTransactionDto(int transaction) throws JsonProcessingException {
        final TransactionDto transactionDto = new TransactionDto();
        transactionDto.setBalance(transaction);
        return objectMapper.writeValueAsString(transactionDto);
    }

    private String clientId(ResultActions resultActions) throws UnsupportedEncodingException, JsonProcessingException {
        final String json = resultActions.andReturn().getResponse().getContentAsString();
        final ClientDTO clientDTO = objectMapper.readValue(json, ClientDTO.class);
        return clientDTO.getId().toString();
    }

}