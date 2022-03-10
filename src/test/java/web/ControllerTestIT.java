package web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

        mvc.perform(head("/bank/v1/clients/"+uuid))
                .andExpect(status().isOk());
    }

    @Test
    public void headClientThatIsNotExist() throws Exception {
        mvc.perform(head("/bank/v1/clients/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

}
