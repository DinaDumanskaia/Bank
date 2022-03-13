package web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static java.net.http.HttpRequest.BodyPublishers.noBody;

public class AcceptanceTest {

    @Test
    public void testLocalHost() throws IOException, URISyntaxException, InterruptedException {
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), checkHead("http://localhost:8080/bank/v1/clients/2406846c-3c01-4297-b8c2-8a960ddefce6"));
    }

    @Test
    public void testPostClientShouldReturn201() throws IOException, URISyntaxException, InterruptedException {
        final HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));

        Assert.assertEquals(HttpStatus.CREATED.value(), response.statusCode());
    }

    @Test
    public void testHeadClient() throws IOException, URISyntaxException, InterruptedException {
        final HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        String id = getClientDTO(response.body()).getId();
        Assert.assertEquals(HttpStatus.OK.value(), checkHead("http://localhost:8080/bank/v1/clients/" + id));
    }

    @Test
    public void testPostClientThatIsNotExist() throws IOException, URISyntaxException, InterruptedException {
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), checkHead("http://localhost:8080/bank/v1/clients/" + UUID.randomUUID()));
    }

    @Test
    public void testCheckCreatedClientBalance() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        Assert.assertEquals(0, getClientDTO(response.body()).getBalance());
    }

    @Test
    public void testChangeBalance() throws URISyntaxException, IOException, InterruptedException {
        int transaction = 100;
        String id = createClientRequest();

        int statusCode = changeBalanceRequest(transaction, id);
        Assert.assertEquals(HttpStatus.OK.value(), statusCode);

        int currentBalance = getCurrentBalanceRequest(id);
        Assert.assertEquals(transaction, currentBalance);
    }

    @Test
    public void testBalanceNotNegative() throws IOException, URISyntaxException, InterruptedException {
        int firstTransaction = 10;
        int secondTransaction = -100;

        String id = createClientRequest();
        changeBalanceRequest(firstTransaction, id);
        int statusCodeAfterSecondTransaction = changeBalanceRequest(secondTransaction, id);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), statusCodeAfterSecondTransaction);

        int currentBalance = getCurrentBalanceRequest(id);
        Assert.assertEquals(firstTransaction, currentBalance);
    }

    private int getCurrentBalanceRequest(String id) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> getBalance = sendRequest(getRequest("http://localhost:8080/bank/v1/clients/" + id + "/balance/"));
        int currentBalance = Integer.parseInt(getBalance.body());
        return currentBalance;
    }

    private int changeBalanceRequest(int transaction, String id) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> putBalanceResponse = sendRequest(createChangeBalanceRequest("http://localhost:8080/bank/v1/clients/" + id + "/balance/", transaction));
        int statusCode = putBalanceResponse.statusCode();
        return statusCode;
    }

    private String createClientRequest() throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        String id = getClientDTO(response.body()).getId();
        return id;
    }

    private int checkHead(String urlInputString) throws IOException, InterruptedException, URISyntaxException {
        return sendRequest(headRequest(urlInputString)).statusCode();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest headRequest(String urlInputString) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .method("HEAD", noBody())
                .build();
    }

    private HttpRequest postRequest(String urlInputString) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .POST(noBody())
                .build();
    }

    private HttpRequest getRequest(String urlInputString) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .GET()
                .build();
    }

    private HttpRequest createChangeBalanceRequest(String urlInputString, int transaction) throws URISyntaxException {
        String requestBody = createJSONChangeBalanceRequestBody(transaction);
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    private String createJSONChangeBalanceRequestBody(int transaction) {
        return "{\"balance\":\"" + transaction + "\"}";
    }

    private ClientDTO getClientDTO(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, ClientDTO.class);
    }
}
