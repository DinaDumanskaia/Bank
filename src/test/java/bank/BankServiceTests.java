package bank;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import web.ClientDTO;
import web.Controller;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class BankServiceTests {
    private BankService bankService;
    ClientDTO client1;
    ClientDTO client2;
    @Before
    public void init() throws Exception {
        bankService = new BankService(new FakeDateProviderImpl());
        client1 = bankService.createNewClient();
        client2 = bankService.createNewClient();
    }

    @Test
    public void testChangeBalanceInRUB() throws Exception {
        int rubleBalance = 100;
        bankService.changeBalance(client1.getId(), Currency.RUB, rubleBalance);

        Assert.assertEquals(rubleBalance, bankService.getBalance(client1.getId(), Currency.RUB));
    }

    @Test
    public void testChangeBalanceInUSD() throws Exception {
        int usdBalance = 70;
        bankService.changeBalance(client1.getId(), Currency.USD, usdBalance);

        Assert.assertEquals(usdBalance, bankService.getBalance(client1.getId(), Currency.USD));
    }

    @Test
    public void testChangeBalanceInEUR() throws Exception {
        int euroBalance = 50;
        bankService.changeBalance(client1.getId(), Currency.EUR, euroBalance);

        Assert.assertEquals(euroBalance, bankService.getBalance(client1.getId(), Currency.EUR));
    }

    @Test
    public void testGetTransactionsReturnACopy() throws Exception {
        bankService.changeBalance(client1.getId(), 50);
        bankService.changeBalance(client1.getId(), 100);

        List<TransactionData> listOfTransactions = bankService.getTransactions(client1.getId());
        listOfTransactions.clear();

        Assert.assertEquals(0, listOfTransactions.size());
        Assert.assertEquals(2, bankService.getTransactions(client1.getId()).size());
    }

    @Test
    public void testTransactionDate() throws Exception {
        bankService.changeBalance(client1.getId(), 50);

        List<TransactionData> listOfTransactions = bankService.getTransactions(client1.getId());
        TransactionData lastTransaction = listOfTransactions.get(listOfTransactions.size() - 1);
        Date date = lastTransaction.getDate();

        Assert.assertEquals(FakeDateProviderImpl.DATE, date);
    }

    @Test
    public void testCreateNewClient() {
        Assert.assertTrue(bankService.clientExists(client1.getId()));
    }

    @Test
    public void testDefaultBalance() {
        assertEquals(0, bankService.getBalance(client1.getId()));
    }

    @Test
    public void testBalanceIncrement() throws Exception {
        bankService.changeBalance(client1.getId(), 100);

        Assert.assertEquals(100, bankService.getBalance(client1.getId()));
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception {
        bankService.changeBalance(client1.getId(), 100);
        bankService.changeBalance(client1.getId(), -20);

        assertEquals(80, bankService.getBalance(client1.getId()));
    }

    @Test(expected = Exception.class)
    public void testOverdraft() throws Exception {
        bankService.changeBalance(client1.getId(), 200);
        bankService.changeBalance(client1.getId(), -300);
    }

    @Test
    public void testAccountStatement() throws Exception {
        bankService.changeBalance(client1.getId(), 200);
        bankService.changeBalance(client1.getId(), -100);
        bankService.changeBalance(client1.getId(), 50);

        List<Integer> transactionValues = getTransactionValues(bankService.getTransactions(client1.getId()));

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
        bankService.changeBalance(client1.getId(), 100);
        bankService.changeBalance(client1.getId(), -50);
        bankService.changeBalance(client2.getId(), 200);
        bankService.changeBalance(client2.getId(), 30);

        List<Integer> transactionValues = getTransactionValues(bankService.getTransactions(client1.getId()));

        Assert.assertEquals(Arrays.asList(100, -50), transactionValues);
    }

    @Test
    public void testWhenBalanceOf1stClientIncreasesBy100TheBalanceOfSecondIs0() throws Exception {
        bankService.changeBalance(client1.getId(), 100);
        Assert.assertEquals(0, bankService.getBalance(client2.getId()));
    }

    @Test
    public void testFirstClientBalanceAfterTransferringMoney() throws Exception {
        bankService.changeBalance(client1.getId(), 100);
        bankService.transferMoney(client1.getId(), client2.getId(), 30);

        Assert.assertEquals(70, bankService.getBalance(client1.getId()));
    }

    @Test
    public void testSecondClientBalanceAfterReceivingMoney() throws Exception {
        bankService.changeBalance(client1.getId(), 100);
        bankService.transferMoney(client1.getId(), client2.getId(), 30);

        Assert.assertEquals(30, bankService.getBalance(client2.getId()));
    }
}