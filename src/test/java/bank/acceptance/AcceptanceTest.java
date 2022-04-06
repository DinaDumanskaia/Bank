package bank.acceptance;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public class AcceptanceTest {
    private final WebClient2 webClient2 = new WebClient2();

    @BeforeClass
    public static void setUp() throws InterruptedException, IOException {
        Infrastructure.startApp();
        Runtime.getRuntime().addShutdownHook(new Thread(Infrastructure::kill));
    }

    @Test
    public void whenClientWasCreated_ShouldExists() throws IOException, URISyntaxException, InterruptedException {
        String clientId = webClient2.createClient();
        Assertions.assertTrue(webClient2.checkClientExists(clientId));
    }

    @Test
    public void whenClientIsNotCreated_ShouldReturnNotFound() throws IOException, URISyntaxException, InterruptedException {
        Assertions.assertFalse(webClient2.checkClientExists(String.valueOf(UUID.randomUUID())));
    }

    @Test
    public void whenClientCreated_ShouldHaveZeroBalance() throws URISyntaxException, IOException, InterruptedException {
        HttpResponse<String> response = webClient2.sendRequest(webClient2.postRequest("http://localhost:8080/bank/v1/clients/"));
        Assert.assertEquals(0, webClient2.getClientBalanceFromJson(response.body()));
    }

    @Test
    public void afterPostingTransaction_BalanceShouldChangeByTransactionAmount() throws URISyntaxException, IOException, InterruptedException {
        int transaction = 100;
        String clientId = webClient2.postClient();

        int statusCode = webClient2.postTransaction(transaction, clientId);
        Assert.assertEquals(HttpStatus.CREATED.value(), statusCode);

        int currentBalance = webClient2.getCurrentBalanceRequest(clientId);
        Assert.assertEquals(transaction, currentBalance);
    }

    @Test
    public void afterPostingTransactionEUR_BalanceShouldChangeByTransactionAmount() throws URISyntaxException, IOException, InterruptedException {
        int transaction = 100;
        String clientId = webClient2.postClient();

        int statusCode = webClient2.sendRequest(webClient2.createChangeBalanceRequest(webClient2.composeURLString(clientId),
                webClient2.createJSONChangeBalanceRequestBodyByCurrency(transaction, "EUR"))).statusCode();
        Assert.assertEquals(HttpStatus.CREATED.value(), statusCode);

        int currentBalance = webClient2.getBalanceByCurrency(clientId, "EUR");
        Assert.assertEquals(transaction, currentBalance);
    }


    @Test
    public void nullAmount() throws URISyntaxException, IOException, InterruptedException {
        Integer transaction = null;
        String clientId = webClient2.postClient();

        int statusCode = webClient2.postTransaction(transaction, clientId);
        Assert.assertEquals(HttpStatus.CREATED.value(), statusCode);

        Integer currentBalance = webClient2.getCurrentBalanceRequest(clientId);
        Assert.assertNotNull(currentBalance);
    }

    @Test
    public void testIfTransactionMakesBalanceNegative_TransactionFailBalanceNotChanging() throws IOException, URISyntaxException, InterruptedException {
        int firstTransaction = 10;
        int secondTransaction = -100;

        String id = webClient2.postClient();
        webClient2.postTransaction(firstTransaction, id);

        int statusCodeAfterSecondTransaction = webClient2.postTransaction(secondTransaction, id);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), statusCodeAfterSecondTransaction);

        int currentBalance = webClient2.getCurrentBalanceRequest(id);
        Assert.assertEquals(firstTransaction, currentBalance);
    }

    @Test
    public void getTransactionsList() throws IOException, URISyntaxException, InterruptedException {
        int firstTransaction = 10;
        int secondTransaction = 500;

        String id = webClient2.postClient();
        webClient2.postTransaction(firstTransaction, id);
        webClient2.postTransaction(secondTransaction, id);

        HttpResponse<String> response = webClient2.sendRequest(webClient2.getRequest("http://localhost:8080/bank/v1/clients/" + id + "/transactions/"));
        Assert.assertEquals(firstTransaction, webClient2.getAmountFromTransaction(response.body(), 0));
        Assert.assertEquals(secondTransaction, webClient2.getAmountFromTransaction(response.body(), 1));
    }

    @Test
    public void checkTransactionDate() throws IOException, URISyntaxException, InterruptedException, ParseException {
        // Given
        int transaction = 10;
        String id = webClient2.postClient();

        // When
        long start = System.currentTimeMillis();
        HttpResponse<String> response = webClient2.getTransactionsResponse(transaction, id);
        long finish = System.currentTimeMillis();

        // Then
        Date date = webClient2.getDateFormat(response);

        Assert.assertTrue(start < date.getTime());
        Assert.assertTrue(finish > date.getTime());
    }

    @Test
    public void testStartCreateChangeBalanceKillStartCheckBalance() throws IOException, URISyntaxException, InterruptedException {
        String clientId = webClient2.postClient();
        webClient2.postTransaction(10, clientId);

        Infrastructure.kill();

        setUp();
        Assert.assertEquals(10, webClient2.getCurrentBalanceRequest(clientId));
    }
}