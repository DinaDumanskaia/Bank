package bank.acceptance;

import bank.domain.Currency;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AcceptanceTest {
    private final WebClient webClient = new WebClient();

    @BeforeClass
    public static void setUp() throws InterruptedException, IOException {
        Infrastructure.startApp();
        Runtime.getRuntime().addShutdownHook(new Thread(Infrastructure::kill));
    }

    @Test
    public void whenClientWasCreated_ShouldExists() throws IOException, URISyntaxException, InterruptedException {
        String clientId = webClient.createClient();
        Assertions.assertTrue(webClient.checkClientExists(clientId));
    }

    @Test
    public void whenClientIsNotCreated_ShouldReturnNotFound() throws IOException, URISyntaxException, InterruptedException {
        Assertions.assertFalse(webClient.checkClientExists(String.valueOf(UUID.randomUUID())));
    }

    @Test
    public void afterPostingTransaction_BalanceShouldChangeByTransactionAmount() throws URISyntaxException, IOException, InterruptedException {
        String clientId = webClient.createClient();

        webClient.changeBalance(100, clientId);

        Assert.assertEquals(100, webClient.getBalance(clientId));
    }

    @Test
    public void afterPostingTransactionEUR_BalanceShouldChangeByTransactionAmount() throws URISyntaxException, IOException, InterruptedException {
        String clientId = webClient.createClient();

        webClient.changeBalanceWithCurrency(100, clientId,"EUR");

        Assert.assertEquals(100, webClient.getBalanceByCurrency(clientId, "EUR"));
    }


    @Test
    public void nullAmount() throws URISyntaxException, IOException, InterruptedException {
        Integer transaction = null;
        String clientId = webClient.createClient();

        webClient.changeBalance(transaction, clientId);

        Integer currentBalance = webClient.getBalance(clientId);
        Assert.assertNotNull(currentBalance);
    }

    @Test
    public void testIfTransactionMakesBalanceNegative_TransactionFailBalanceNotChanging() throws IOException, URISyntaxException, InterruptedException {
        String id = webClient.createClient();
        webClient.changeBalance(10, id);

        webClient.changeBalance(-100, id);

        Assert.assertEquals(10, webClient.getBalance(id));
    }

    @Test
    public void getRUBTransactionsAmountList() throws IOException, URISyntaxException, InterruptedException {
        String id = webClient.createClient();
        webClient.changeBalance(10, id);
        webClient.changeBalance(500, id);

        List<Integer> listOfAmounts = webClient.getListOfTransactionAmounts(id);

        assertEquals(10, listOfAmounts.get(0));
        assertEquals(500, listOfAmounts.get(1));
    }

    @Test
    public void getEURTransactionAmountsList() throws IOException, URISyntaxException, InterruptedException {
        String id = webClient.createClient();
        webClient.changeBalance(10, id, "EUR");
        webClient.changeBalance(500, id, "EUR");

        List<Integer> listOfAmounts = webClient.getListOfTransactionAmountsV2(id, "EUR");

        assertEquals(10, listOfAmounts.get(0));
        assertEquals(500, listOfAmounts.get(1));
    }

    @Test
    public void checkTransactionDate() throws IOException, URISyntaxException, InterruptedException, ParseException {
        String id = webClient.createClient();

        long start = System.currentTimeMillis();
        webClient.changeBalance(100, id);
        long finish = System.currentTimeMillis();

        Date date = webClient.getFirstTransactionDate(id);
        Assert.assertTrue(start <= date.getTime());
        Assert.assertTrue(finish >= date.getTime());
    }

    @Test
    public void testStartCreateChangeBalanceKillStartCheckBalance() throws IOException, URISyntaxException, InterruptedException {
        String clientId = webClient.createClient();
        webClient.changeBalance(10, clientId);

        Infrastructure.kill();
        Infrastructure.startApp();

        Assert.assertEquals(10, webClient.getBalance(clientId));
    }
}