import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BankServiceTests {
    private BankService bankService;
    @Before
    public void init() {
        bankService = new BankService(new MockDateProviderImpl());
        bankService.createNewClient("+7");
        bankService.createNewClient("+79");
    }

    @Test
    public void testChangeBalanceInRUB() throws Exception {
        int rubleBalance = 100;
        bankService.changeBalance("+7", Currency.RUB, rubleBalance);

        Assert.assertEquals(rubleBalance, bankService.getBalance("+7", Currency.RUB));
    }

    @Test
    public void testChangeBalanceInUSD() throws Exception {
        int usdBalance = 70;
        bankService.changeBalance("+7", Currency.USD, usdBalance);

        Assert.assertEquals(usdBalance, bankService.getBalance("+7", Currency.USD));
    }

    @Test
    public void testChangeBalanceInEUR() throws Exception {
        int euroBalance = 50;
        bankService.changeBalance("+7", Currency.EUR, euroBalance);

        Assert.assertEquals(euroBalance, bankService.getBalance("+7", Currency.EUR));
    }

    @Test
    public void testGetTransactionsReturnACopy() throws Exception {
        bankService.changeBalance("+7", 50);
        bankService.changeBalance("+7", 100);

        List<TransactionData> listOfTransactions = bankService.getTransactions("+7");
        listOfTransactions.clear();

        Assert.assertEquals(0, listOfTransactions.size());
        Assert.assertEquals(2, bankService.getTransactions("+7").size());
    }

    @Test
    public void testTransactionDate() throws Exception {
        bankService.changeBalance("+7", 50);

        List<TransactionData> listOfTransactions = bankService.getTransactions("+7");
        TransactionData lastTransaction = listOfTransactions.get(listOfTransactions.size() - 1);
        Date date = lastTransaction.getDate();

        Assert.assertEquals(MockDateProviderImpl.DATE, date);
    }

    @Test
    public void testCreateNewClient() {
        Assert.assertTrue(bankService.clientExists("+7"));
    }

    @Test
    public void testDefaultBalance() {
        assertEquals(0, bankService.getBalance("+7"));
    }

    @Test
    public void testBalanceIncrement() throws Exception {
        bankService.changeBalance("+7", 100);

        Assert.assertEquals(100, bankService.getBalance("+7"));
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception {
        bankService.changeBalance("+7", 100);
        bankService.changeBalance("+7", -20);

        assertEquals(80, bankService.getBalance("+7"));
    }

    @Test(expected = Exception.class)
    public void testOverdraft() throws Exception {
        bankService.changeBalance("+7", 200);
        bankService.changeBalance("+7", -300);
    }

    @Test
    public void testAccountStatement() throws Exception {
        bankService.changeBalance("+7", 200);
        bankService.changeBalance("+7", -100);
        bankService.changeBalance("+7", 50);

        List<Integer> transactionValues = getTransactionValues(bankService.getTransactions("+7"));

        Assert.assertEquals(Arrays.asList(200, -100, 50), transactionValues);
    }

    private List<Integer> getTransactionValues(List<TransactionData> accountStatement) {
        List<Integer> values = new ArrayList<>();
        for (TransactionData transactionData : accountStatement) {
            values.add(transactionData.getValue());
        }
        return values;
    }

    @Test
    public void testAccountStatement2() throws Exception {
        bankService.changeBalance("+7", 100);
        bankService.changeBalance("+7", -50);
        bankService.changeBalance("+79", 200);
        bankService.changeBalance("+79", 30);

        List<Integer> transactionValues = getTransactionValues(bankService.getTransactions("+7"));

        Assert.assertEquals(Arrays.asList(100, -50), transactionValues);
    }

    @Test
    public void testWhenBalanceOf1stClientIncreasesBy100TheBalanceOfSecondIs0() throws Exception {
        bankService.changeBalance("+7", 100);
        Assert.assertEquals(0, bankService.getBalance("+79"));
    }

    @Test
    public void testFirstClientBalanceAfterTransferringMoney() throws Exception {
        bankService.changeBalance("+7", 100);
        bankService.transferMoney("+7", "+79", 30);

        Assert.assertEquals(70, bankService.getBalance("+7"));
    }

    @Test
    public void testSecondClientBalanceAfterReceivingMoney() throws Exception {
        bankService.changeBalance("+7", 100);
        bankService.transferMoney("+7", "+79", 30);

        Assert.assertEquals(30, bankService.getBalance("+79"));
    }
}