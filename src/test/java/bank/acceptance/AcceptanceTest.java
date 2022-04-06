package bank.acceptance;

import bank.domain.Currency;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
            Process exec = Runtime.getRuntime().exec("cmd /c netstat -aon | find \"8080\" | find \"LISTEN\"");
            List<String> outputLines = outputLines(exec.getInputStream());
            if (!outputLines.isEmpty()) return getPid(outputLines);
            System.out.println("App has not started yet");
            sleep(1000);
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
            Process proc = Runtime.getRuntime().exec("cmd /c mvn exec:java");

            ProcessHandler inputStream = new ProcessHandler(proc.getInputStream(), "INPUT");
            ProcessHandler errorStream = new ProcessHandler(proc.getErrorStream(), "ERROR");
            /* start the stream threads */
            inputStream.start();
            errorStream.start();

            //outputLines(proc.getErrorStream()).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> outputLines(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines().toList();
    }

    @Test
    public void whenClientWasCreated_ShouldExists() throws IOException, URISyntaxException, InterruptedException {
        final HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        String id = getClientIdFromJson(response.body());
        Assert.assertEquals(HttpStatus.OK.value(), checkHead("http://localhost:8080/bank/v1/clients/" + id));
    }

    @Test
    public void whenClientIsNotCreated_ShouldReturnNotFound() throws IOException, URISyntaxException, InterruptedException {
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), checkHead("http://localhost:8080/bank/v1/clients/" + UUID.randomUUID()));
    }

    @Test
    public void whenClientCreated_ShouldHaveZeroBalance() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));
        Assert.assertEquals(0, getClientBalanceFromJson(response.body()));
    }

    @Test
    public void afterPostingTransaction_BalanceShouldChangeByTransactionAmount() throws URISyntaxException, IOException, InterruptedException {
        int transaction = 100;
        String clientId = postClient();

        int statusCode = postTransaction(transaction, clientId);
        Assert.assertEquals(HttpStatus.CREATED.value(), statusCode);

        int currentBalance = getCurrentBalanceRequest(clientId);
        Assert.assertEquals(transaction, currentBalance);
    }

    @Test
    public void afterPostingTransactionEUR_BalanceShouldChangeByTransactionAmount() throws URISyntaxException, IOException, InterruptedException {
        int transaction = 100;
        String clientId = postClient();

        int statusCode = sendRequest(createChangeBalanceRequest(composeURLString(clientId),
                createJSONChangeBalanceRequestBodyByCurrency(transaction, Currency.EUR))).statusCode();
        Assert.assertEquals(HttpStatus.CREATED.value(), statusCode);

        int currentBalance = getBalanceByCurrency(clientId, Currency.EUR);
        Assert.assertEquals(transaction, currentBalance);
    }

    private String composeURLString(String clientId) {
        return  "http://localhost:8080/bank/v2/clients/" + clientId + "/transactions/";
    }

    private int getBalanceByCurrency(String clientId, Currency currency) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> clientResponse = getClientResponse(clientId);
        JsonNode jsonNode = new ObjectMapper().readTree(clientResponse.body());
        return jsonNode.get("accounts").get(currency.name()).asInt();
    }

    private HttpResponse<String> getClientResponse(String clientId) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> clientResponse = sendRequest(HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/bank/v2/clients/" + clientId))
                .GET()
                .build());
        return clientResponse;
    }


    @Test
    public void nullAmount() throws URISyntaxException, IOException, InterruptedException {
        Integer transaction = null;
        String clientId = postClient();

        int statusCode = postTransaction(transaction, clientId);
        Assert.assertEquals(HttpStatus.CREATED.value(), statusCode);

        Integer currentBalance = getCurrentBalanceRequest(clientId);
        Assert.assertNotNull(currentBalance);
    }

    @Test
    public void testIfTransactionMakesBalanceNegative_TransactionFailBalanceNotChanging() throws IOException, URISyntaxException, InterruptedException {
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
    public void checkTransactionDate() throws IOException, URISyntaxException, InterruptedException, ParseException {
        // Given
        int transaction = 10;
        String id = postClient();

        // When
        long start = System.currentTimeMillis();
        HttpResponse<String> response = getTransactionsResponse(transaction, id);
        long finish = System.currentTimeMillis();

        // Then
        Date date = getDateFormat(response);

        Assert.assertTrue(start < date.getTime());
        Assert.assertTrue(finish > date.getTime());
    }

    private Date getDateFormat(HttpResponse<String> response) throws ParseException, JsonProcessingException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zz yyyy", Locale.US);
        return simpleDateFormat.parse(getDateFromTransaction(response.body(), 0));
    }

    private HttpResponse<String> getTransactionsResponse(int transaction, String id) throws InterruptedException, IOException, URISyntaxException {
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        postTransaction(transaction, id);
        return sendRequest(getRequest("http://localhost:8080/bank/v1/clients/" + id + "/transactions/"));
    }

    @Test
    public void testStartCreateChangeBalanceKillStartCheckBalance() throws IOException, URISyntaxException, InterruptedException {
        String clientId = postClient();
        postTransaction(10, clientId);

        kill();

        setUp();
        Assert.assertEquals(10, getCurrentBalanceRequest(clientId));
    }


    private int getCurrentBalanceRequest(String id) throws IOException, InterruptedException, URISyntaxException {
        HttpResponse<String> clientResponse = sendRequest(getRequest("http://localhost:8080/bank/v1/clients/" + id));
        return getClientBalanceFromJson(clientResponse.body());
    }

    private int postTransaction(Integer transaction, String id) throws IOException, InterruptedException, URISyntaxException {
        String requestBody = createJSONChangeBalanceRequestBody(transaction);
        HttpResponse<String> postBalanceResponse =
                sendRequest(createChangeBalanceRequest(composeTransactionUrl(id), requestBody));
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

    private String getDateFromTransaction(String jsonBody, int transactionIndex) throws JsonProcessingException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonBody);
        return jsonNode.get(transactionIndex).get("date").asText();
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

    private HttpRequest createChangeBalanceRequest(String urlInputString, String requestBody) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .build();
    }

    private String createJSONChangeBalanceRequestBody(Integer transaction) {
        return "{\"amount\":\"" + transaction + "\"}";
    }

    private String createJSONChangeBalanceRequestBodyByCurrency(Integer transaction, Currency currency) {
        return "{\"amount\":\"" + transaction + "\", \"currency\":\"" + currency.name() + "\"}";
    }

    static class ProcessHandler extends Thread {
        InputStream inputStream;
        String streamType;

        public ProcessHandler(InputStream inputStream, String streamType) {
            this.inputStream = inputStream;
            this.streamType = streamType;
        }

        public void run() {
            try {
                InputStreamReader inpStrd = new InputStreamReader(inputStream);
                BufferedReader buffRd = new BufferedReader(inpStrd);
                String line = null;
                while ((line = buffRd.readLine()) != null) {
                    System.out.println(streamType+ "::" + line);
                }
                buffRd.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}