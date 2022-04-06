package bank.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.net.http.HttpRequest.BodyPublishers.noBody;

public class WebClient2 {
    public WebClient2() {
    }

    HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpRequest headRequest(String urlInputString) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .method("HEAD", noBody())
                .build();
    }

    HttpRequest postRequest(String urlInputString) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .POST(noBody())
                .build();
    }

    HttpRequest getRequest(String urlInputString) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .GET()
                .build();
    }

    Integer checkHead(String urlInputString) throws IOException, InterruptedException, URISyntaxException {
        return sendRequest(headRequest(urlInputString)).statusCode();
    }

    int getClientBalanceFromJson(String jsonBody) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        return jsonNode.get("balance").asInt();
    }

    String getClientIdFromJson(String jsonBody) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        return jsonNode.get("id").asText();
    }

    String createClient() throws IOException, InterruptedException, URISyntaxException {
        final HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        return getClientIdFromJson(response.body());
    }

    String postClient() throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        return getClientIdFromJson(response.body());
    }

    String getDateFromTransaction(String jsonBody, int transactionIndex) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        return jsonNode.get(transactionIndex).get("date").asText();
    }

    int getBalance(String id) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> clientResponse = sendRequest(getRequest("http://localhost:8080/bank/v1/clients/" + id));
        return getClientBalanceFromJson(clientResponse.body());
    }

    Date getDateFormat(HttpResponse<String> response) throws ParseException, JsonProcessingException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zz yyyy", Locale.US);
        return simpleDateFormat.parse(getDateFromTransaction(response.body(), 0));
    }

    String composeURLString(String clientId) {
        return  "http://localhost:8080/bank/v2/clients/" + clientId + "/transactions/";
    }

    HttpResponse<String> getClientResponse(String clientId) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> clientResponse = sendRequest(HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/bank/v2/clients/" + clientId))
                .GET()
                .build());
        return clientResponse;
    }

    boolean checkClientExists(String clientId) throws IOException, InterruptedException, URISyntaxException {
        return checkHead("http://localhost:8080/bank/v1/clients/" + clientId).equals(HttpStatus.OK.value());
    }

    String createJSONChangeBalanceRequestBodyByCurrency(Integer transaction, String currency) {
        return "{\"amount\":\"" + transaction + "\", \"currency\":\"" + currency + "\"}";
    }

    String createJSONChangeBalanceRequestBody(Integer transaction) {
        return "{\"amount\":\"" + transaction + "\"}";
    }

    int changeBalance(Integer transaction, String id) throws IOException, InterruptedException, URISyntaxException {
        return changeBalance(transaction, id, "RUB");
    }

    int changeBalance(Integer transaction, String id, String currency) throws IOException, InterruptedException, URISyntaxException {
        String requestBody = createJSONChangeBalanceRequestBodyByCurrency(transaction, currency);
        HttpResponse<String> postBalanceResponse =
                sendRequest(createChangeBalanceRequest(composeTransactionUrl(id), requestBody));
        return postBalanceResponse.statusCode();
    }

    HttpResponse<String> changeBalanceWithCurrency(int transaction, String clientId, String currency) throws IOException, InterruptedException, URISyntaxException {
        return sendRequest(createChangeBalanceRequest(composeURLString(clientId),
                createJSONChangeBalanceRequestBodyByCurrency(transaction, currency)));
    }

    HttpRequest createChangeBalanceRequest(String urlInputString, String requestBody) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
    }

    String composeTransactionUrl(String id) {
        return "http://localhost:8080/bank/v1/clients/" + id + "/transactions/";
    }

    HttpResponse<String> getTransactionsResponse(int transaction, String id) throws InterruptedException, IOException, URISyntaxException {
        return sendRequest(getRequest("http://localhost:8080/bank/v1/clients/" + id + "/transactions/"));
    }

    List<Integer> getListOfTransactionAmounts(String id) throws IOException, InterruptedException, URISyntaxException {
        List<Integer> list = new ArrayList<>();
        JsonNode jsonNode = new ObjectMapper().readTree(getTransactionJson(id));
        if (jsonNode.isArray()) {
            for (JsonNode arrayItem : jsonNode) {
                list.add(arrayItem.get("amount").intValue());
            }
        }
        return list;
    }

    int getAmountFromTransaction(String jsonBody, int transactionIndex) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        return jsonNode.get(transactionIndex).get("amount").asInt();
    }

    int getBalanceByCurrency(String clientId, String currency) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> clientResponse = getClientResponse(clientId);
        JsonNode jsonNode = new ObjectMapper().readTree(clientResponse.body());
        return jsonNode.get("accounts").get(currency).asInt();
    }

    String getTransactionJson(String id) throws IOException, InterruptedException, URISyntaxException {
        return sendRequest(getRequest("http://localhost:8080/bank/v1/clients/" + id + "/transactions/")).body();
    }

    Date getFirstTransactionDate(String id) throws ParseException, InterruptedException, IOException, URISyntaxException {
        return getDateFormat(getTransactionsResponse(10, id));
    }
}