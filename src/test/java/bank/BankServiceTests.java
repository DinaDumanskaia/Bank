package bank;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class BankServiceTests {
    private final BankService bankService = new BankService(new FakeDateProviderImpl());
    private final Client client1 = bankService.createNewClient();
    private final Client client2 = bankService.createNewClient();

    @Test
    public void testChangeBalanceInRUB() throws Exception {
        int rubleBalance = 100;
        bankService.changeBalance(client1.getID(), Currency.RUB, rubleBalance);

        Assert.assertEquals(rubleBalance, bankService.getBalance(client1.getID(), Currency.RUB));
    }

    @Test
    public void testChangeBalanceInUSD() throws Exception {
        int usdBalance = 70;
        bankService.changeBalance(client1.getID(), Currency.USD, usdBalance);

        Assert.assertEquals(usdBalance, bankService.getBalance(client1.getID(), Currency.USD));
    }

    @Test
    public void testChangeBalanceInEUR() throws Exception {
        int euroBalance = 50;
        bankService.changeBalance(client1.getID(), Currency.EUR, euroBalance);

        Assert.assertEquals(euroBalance, bankService.getBalance(client1.getID(), Currency.EUR));
    }

    @Test
    public void testGetTransactionsReturnACopy() throws Exception {
        bankService.changeBalance(client1.getID(), 50);
        bankService.changeBalance(client1.getID(), 100);

        List<TransactionData> listOfTransactions = bankService.getTransactions(client1.getID());
        listOfTransactions.clear();

        Assert.assertEquals(0, listOfTransactions.size());
        Assert.assertEquals(2, bankService.getTransactions(client1.getID()).size());
    }

    @Test
    public void testTransactionDate() throws Exception {
        bankService.changeBalance(client1.getID(), 50);

        List<TransactionData> listOfTransactions = bankService.getTransactions(client1.getID());
        TransactionData lastTransaction = listOfTransactions.get(listOfTransactions.size() - 1);
        Date date = lastTransaction.getDate();

        Assert.assertEquals(FakeDateProviderImpl.DATE, date);
    }

    @Test
    public void testCreateNewClient() {
        Assert.assertTrue(bankService.clientExists(client1.getID()));
    }

    @Test
    public void testDefaultBalance() {
        assertEquals(0, bankService.getBalance(client1.getID()));
    }

    @Test
    public void testBalanceIncrement() throws Exception {
        bankService.changeBalance(client1.getID(), 100);

        Assert.assertEquals(100, bankService.getBalance(client1.getID()));
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception {
        bankService.changeBalance(client1.getID(), 100);
        bankService.changeBalance(client1.getID(), -20);

        assertEquals(80, bankService.getBalance(client1.getID()));
    }

    @Test(expected = NegativeBalanceException.class)
    public void testOverdraft() throws Exception {
        bankService.changeBalance(client1.getID(), 200);
        bankService.changeBalance(client1.getID(), -300);
    }

    @Test
    public void testAccountStatement() throws Exception {
        bankService.changeBalance(client1.getID(), 200);
        bankService.changeBalance(client1.getID(), -100);
        bankService.changeBalance(client1.getID(), 50);

        List<Integer> transactionValues = getTransactionValues(bankService.getTransactions(client1.getID()));

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
        bankService.changeBalance(client1.getID(), 100);
        bankService.changeBalance(client1.getID(), -50);
        bankService.changeBalance(client2.getID(), 200);
        bankService.changeBalance(client2.getID(), 30);

        List<Integer> transactionValues = getTransactionValues(bankService.getTransactions(client1.getID()));

        Assert.assertEquals(Arrays.asList(100, -50), transactionValues);
    }

    @Test
    public void testWhenBalanceOf1stClientIncreasesBy100TheBalanceOfSecondIs0() throws Exception {
        bankService.changeBalance(client1.getID(), 100);
        Assert.assertEquals(0, bankService.getBalance(client2.getID()));
    }

    @Test
    public void testFirstClientBalanceAfterTransferringMoney() throws Exception {
        bankService.changeBalance(client1.getID(), 100);
        bankService.transferMoney(client1.getID(), client2.getID(), 30);

        Assert.assertEquals(70, bankService.getBalance(client1.getID()));
    }

    @Test
    public void testSecondClientBalanceAfterReceivingMoney() throws Exception {
        bankService.changeBalance(client1.getID(), 100);
        bankService.transferMoney(client1.getID(), client2.getID(), 30);

        Assert.assertEquals(30, bankService.getBalance(client2.getID()));
    }
}