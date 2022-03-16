package web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static java.net.http.HttpRequest.BodyPublishers.noBody;

public class AcceptanceTest {
    @BeforeClass
    public static void setUp() throws InterruptedException, IOException {
        new Thread(AcceptanceTest::runApp).start();
        int pid = getPid();
        Runtime.getRuntime().addShutdownHook(new Thread(AcceptanceTest::kill));
        System.out.println("App has started. Pid is " + pid);
    }

    private static void kill() {
        try {
            Process exec = Runtime.getRuntime().exec("cmd /c taskkill /F /PID " + getPid());
            outputLines(exec.getInputStream()).forEach(System.out::println);
            outputLines(exec.getErrorStream()).forEach(System.out::println);

            System.out.println("Application has been killed");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int getPid() throws IOException, InterruptedException {
        for (int i = 0; i <= 10; i++) {
            sleep(5000);
            Process exec = Runtime.getRuntime().exec("cmd /c netstat -aon | find \"8080\" | find \"LISTEN\"");
            List<String> outputLines = outputLines(exec.getInputStream());
            if (! outputLines.isEmpty()) return getPid(outputLines);
            System.out.println("App has not started yet");
        }
        throw new RuntimeException("App has not started");
    }

    private static int getPid8082() throws IOException, InterruptedException {
        for (int i = 0; i <= 10; i++) {
            sleep(5000);
            Process exec = Runtime.getRuntime().exec("cmd /c netstat -aon | find \"8082\" | find \"LISTEN\"");
            List<String> outputLines = outputLines(exec.getInputStream());
            if (! outputLines.isEmpty()) return getPid(outputLines);
            System.out.println("App has not started yet");
        }
        throw new RuntimeException("App has not started");
    }

    private static int getPid(List<String> lines) {
        String firstLine = lines.get(0);
        String[] words = firstLine.split("\\s+");
        return Integer.parseInt(words[5]);
    }

    private static void runApp() {
        try {
            Process exec = Runtime.getRuntime().exec("cmd /c mvn exec:java");
            outputLines(exec.getErrorStream()).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> outputLines(InputStream inputStream) {
        BufferedReader reader=new BufferedReader( new InputStreamReader(inputStream));
        return reader.lines().toList();
    }


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
        String id = getClientIdFromJson(response.body());
        Assert.assertEquals(HttpStatus.OK.value(), checkHead("http://localhost:8080/bank/v1/clients/" + id));
    }

    @Test
    public void testPostClientThatIsNotExist() throws IOException, URISyntaxException, InterruptedException {
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), checkHead("http://localhost:8080/bank/v1/clients/" + UUID.randomUUID()));
    }

    @Test
    public void testCheckCreatedClientBalance() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        Assert.assertEquals(0, getClientBalanceFromJson(response.body()));
    }


    @Test
    public void testChangeBalance() throws URISyntaxException, IOException, InterruptedException {
        int transaction = 100;
        String clientId = postClient();

        int statusCode = postTransaction(transaction, clientId);
        Assert.assertEquals(HttpStatus.CREATED.value(), statusCode);

        int currentBalance = getCurrentBalanceRequest(clientId);
        Assert.assertEquals(transaction, currentBalance);
    }

    @Test
    public void testBalanceNotNegative() throws IOException, URISyntaxException, InterruptedException {
        int firstTransaction = 10;
        int secondTransaction = -100;

        String id = postClient();
        postTransaction(firstTransaction, id);

        int statusCodeAfterSecondTransaction = postTransaction(secondTransaction, id);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), statusCodeAfterSecondTransaction);

        int currentBalance = getCurrentBalanceRequest(id);
        Assert.assertEquals(firstTransaction, currentBalance);
    }

    @Test
    public void getTransactionsList() throws IOException, URISyntaxException, InterruptedException {
        int firstTransaction = 10;
        int secondTransaction = 500;

        String id = postClient();
        postTransaction(firstTransaction, id);
        postTransaction(secondTransaction, id);

        HttpResponse<String> response = sendRequest(getRequest("http://localhost:8080/bank/v1/clients/" + id + "/transactions/"));
        Assert.assertEquals(firstTransaction, getAmountFromTransaction(response.body(), 0));
        Assert.assertEquals(secondTransaction, getAmountFromTransaction(response.body(), 1));
    }

    @Test
    public void testStartCreateChangeBalanceKillStartCheckBalance() throws IOException, URISyntaxException, InterruptedException {
        String clientId = postClient();
        postTransaction(10, clientId);

        kill();

        setUp();
        Assert.assertEquals(10, getCurrentBalanceRequest(clientId));
    }

    @Test
    public void testUserPort8082() throws IOException, InterruptedException {
        System.out.println(getPid8082());
    }


    private int getCurrentBalanceRequest(String id) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> clientResponse = sendRequest(getRequest("http://localhost:8080/bank/v1/clients/" + id));
        return getClientBalanceFromJson(clientResponse.body());
    }

    private int postTransaction(int transaction, String id) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> postBalanceResponse =
                sendRequest(createChangeBalanceRequest(composeTransactionUrl(id), transaction));
        return postBalanceResponse.statusCode();
    }

    private String composeTransactionUrl(String id) {
        return "http://localhost:8080/bank/v1/clients/" + id + "/transactions/";
    }

    private String postClient() throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        return getClientIdFromJson(response.body());
    }

    private String getClientIdFromJson(String jsonBody) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        return jsonNode.get("id").asText();
    }

    private int getAmountFromTransaction(String jsonBody, int transactionIndex) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        return jsonNode.get(transactionIndex).get("amount").asInt();
    }

    private int getClientBalanceFromJson(String jsonBody) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        return jsonNode.get("balance").asInt();
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
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
    }

    private String createJSONChangeBalanceRequestBody(int transaction) {
        return "{\"amount\":\"" + transaction + "\"}";
    }

}
