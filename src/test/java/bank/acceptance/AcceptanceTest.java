package bank.acceptance;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void afterPostingTransaction_BalanceShouldChangeByTransactionAmount() throws URISyntaxException, IOException, InterruptedException {
        String clientId = webClient2.createClient();

        webClient2.changeBalance(100, clientId);

        Assert.assertEquals(100, webClient2.getBalance(clientId));
    }

    @Test
    public void afterPostingTransactionEUR_BalanceShouldChangeByTransactionAmount() throws URISyntaxException, IOException, InterruptedException {
        String clientId = webClient2.postClient();

        webClient2.changeEuroBalance(100, clientId,"EUR");

        Assert.assertEquals(100, webClient2.getBalanceByCurrency(clientId, "EUR"));
    }


    @Test
    public void nullAmount() throws URISyntaxException, IOException, InterruptedException {
        Integer transaction = null;
        String clientId = webClient2.createClient();

        webClient2.changeBalance(transaction, clientId);

        Integer currentBalance = webClient2.getBalance(clientId);
        Assert.assertNotNull(currentBalance);
    }

    @Test
    public void testIfTransactionMakesBalanceNegative_TransactionFailBalanceNotChanging() throws IOException, URISyntaxException, InterruptedException {
        String id = webClient2.createClient();
        webClient2.changeBalance(10, id);

        webClient2.changeBalance(-100, id);

        Assert.assertEquals(10, webClient2.getBalance(id));
    }

    @Test
    public void getRUBTransactionsList() throws IOException, URISyntaxException, InterruptedException {
        String id = webClient2.createClient();
        webClient2.changeBalance(10, id);
        webClient2.changeBalance(500, id);

        List<Integer> listOfAmounts = webClient2.getListOfAmounts(id);

        assertEquals(10, listOfAmounts.get(0));
        assertEquals(500, listOfAmounts.get(1));
    }

    @Test
    public void checkTransactionDate() throws IOException, URISyntaxException, InterruptedException, ParseException {
        // Given
        int transaction = 10;
        String id = webClient2.createClient();

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
        String clientId = webClient2.createClient();
        webClient2.changeBalance(10, clientId);

        Infrastructure.kill();

        setUp();
        Assert.assertEquals(10, webClient2.getBalance(clientId));
    }
}