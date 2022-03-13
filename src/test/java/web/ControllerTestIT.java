package web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.*;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerTestIT {
    @Autowired
    MockMvc mvc;

    @Test
    public void postClient() throws Exception {
        mvc.perform(post("/bank/v1/clients/"))
                .andExpect(status().isCreated());
    }

    @Test
    public void headClient() throws Exception {
        final MvcResult mvcResult = mvc.perform(post("/bank/v1/clients/")).andReturn();

        final String uuid = mvcResult.getResponse().getContentAsString();

        mvc.perform(head("/bank/v1/clients/" + uuid))
                .andExpect(status().isOk());
    }

    @Test
    public void headClientThatIsNotExist() throws Exception {
        mvc.perform(head("/bank/v1/clients/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

}
