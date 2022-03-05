package web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTestIT {
    @Autowired
    private TestRestTemplate template;

    @Test
    public void testCreateClient() {
        String id = "7";
        ResponseEntity<String> response = template.getForEntity("/create_client?id=" + id, String.class);
        assertThat(response.getBody()).isEqualTo("New client with id =" + id + " has been successfully created");
    }

    @Test
    public void checkClientExists() {
        String id = "7";
        template.getForEntity("/create_client?id=" + id, String.class);
        ResponseEntity<String> response = template.getForEntity("/client_exists?id=" + id, String.class);
        assertThat(response.getBody()).isEqualTo("Client with id = " + id + " found!");
    }

    @Test
    public  void checkClientDoesNotExists() {
        ResponseEntity<String> response = template.getForEntity("/client_exists?id=8", String.class);
        assertThat(response.getBody()).isEqualTo("Client not found");
    }

    @Test
    public void getClientMoneyAccountBalance() throws Exception {
        ResponseEntity<String> response = template.getForEntity("/get_balance?id=7&currency=rub", String.class);
        assertThat(response.getBody()).isEqualTo(String.valueOf(0));
    }
}
