package bank.unit;

import bank.application.BankService;
import bank.application.exceptions.IllegalClientIdException;
import bank.domain.Client;
import bank.application.exceptions.ClientNotFoundException;
import bank.domain.Currency;
import bank.domain.NegativeBalanceException;
import bank.domain.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class BankServiceTests {
    private final BankService bankService = new BankService(new FakeDateProviderImpl(), new FakeClientRepository());
    private final UUID clientId1 = bankService.createNewClient().getID();
    private final UUID clientId2 = bankService.createNewClient().getID();

    @Test
    public void testChangeBalanceInRUB() throws IllegalClientIdException {
        int rubleBalance = 100;
        bankService.changeBalance(clientId1, bank.domain.Currency.RUB, rubleBalance);

        Assert.assertEquals(rubleBalance, bankService.getBalance(clientId1, bank.domain.Currency.RUB));
    }

    @Test
    public void testGetClient() throws Exception, IllegalClientIdException {
        int rubleBalance = 100;
        bankService.changeBalance(clientId1, bank.domain.Currency.RUB, rubleBalance);
        Client client = bankService.getClientById(clientId1);

        Assert.assertEquals(rubleBalance, client.getBalance());
    }

    @Test
    public void testChangeBalanceInUSD() throws Exception, IllegalClientIdException {
        int usdBalance = 70;
        bankService.changeBalance(clientId1, Currency.USD, usdBalance);

        Assert.assertEquals(usdBalance, bankService.getBalance(clientId1, bank.domain.Currency.USD));
    }

    @Test
    public void testChangeBalanceInEUR() throws Exception, IllegalClientIdException {
        int euroBalance = 50;
        bankService.changeBalance(clientId1, bank.domain.Currency.EUR, euroBalance);

        Assert.assertEquals(euroBalance, bankService.getBalance(clientId1, bank.domain.Currency.EUR));
    }

    @Test
    public void testGetTransactionsReturnACopy() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 50);
        bankService.changeBalance(clientId1, 100);

        List<Transaction> listOfTransactions = bankService.getTransactions(clientId1);
        listOfTransactions.clear();

        Assert.assertEquals(0, listOfTransactions.size());
        Assert.assertEquals(2, bankService.getTransactions(clientId1).size());
    }

    @Test
    public void testTransactionDate() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 50);

        List<Transaction> listOfTransactions = bankService.getTransactions(clientId1);
        Transaction lastTransaction = listOfTransactions.get(listOfTransactions.size() - 1);
        Date date = lastTransaction.getDate();

        assertEquals(FakeDateProviderImpl.DATE, date);
    }

    @Test
    public void testCreateNewClient() {
        Assert.assertTrue(bankService.clientExists(clientId1));
    }

    @Test(expected = ClientNotFoundException.class)
    public void testClientNotFound() throws IllegalClientIdException {
        bankService.getClientById(UUID.randomUUID());
    }

    @Test
    public void testDefaultBalance() throws IllegalClientIdException {
        assertEquals(0, bankService.getBalance(clientId1));
    }

    @Test
    public void testBalanceIncrement() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 100);

        Assert.assertEquals(100, bankService.getBalance(clientId1));
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 100);
        bankService.changeBalance(clientId1, -20);

        assertEquals(80, bankService.getBalance(clientId1));
    }

    @Test(expected = NegativeBalanceException.class)
    public void testOverdraft() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 200);
        bankService.changeBalance(clientId1, -300);
    }

    @Test
    public void testAccountStatement() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 200);
        bankService.changeBalance(clientId1, -100);
        bankService.changeBalance(clientId1, 50);

        List<Integer> transactionValues = getTransactionValues(bankService.getTransactions(clientId1));

        Assert.assertEquals(Arrays.asList(200, -100, 50), transactionValues);
    }

    private List<Integer> getTransactionValues(List<Transaction> accountStatement) {
        List<Integer> values = new ArrayList<>();
        for (Transaction transactionData : accountStatement) {
            values.add(transactionData.getAmount());
        }
        return values;
    }

    @Test
    public void testAccountStatement2() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 100);
        bankService.changeBalance(clientId1, -50);
        bankService.changeBalance(clientId2, 200);
        bankService.changeBalance(clientId2, 30);

        List<Integer> transactionValues = getTransactionValues(bankService.getTransactions(clientId1));

        Assert.assertEquals(Arrays.asList(100, -50), transactionValues);
    }

    @Test
    public void testWhenBalanceOf1stClientIncreasesBy100TheBalanceOfSecondIs0() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 100);
        Assert.assertEquals(0, bankService.getBalance(clientId2));
    }

    @Test
    public void testFirstClientBalanceAfterTransferringMoney() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 100);
        bankService.transferMoney(clientId1, clientId2, 30);

        Assert.assertEquals(70, bankService.getBalance(clientId1));
    }

    @Test
    public void testSecondClientBalanceAfterReceivingMoney() throws Exception, IllegalClientIdException {
        bankService.changeBalance(clientId1, 100);
        bankService.transferMoney(clientId1, clientId2, 30);

        Assert.assertEquals(30, bankService.getBalance(clientId2));
    }
}