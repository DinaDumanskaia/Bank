import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Tests {
    private BankService bankService;

    @Before
    public void init() {
        bankService = new BankService();
        bankService.createNewClient("+7");
        bankService.createNewClient("+79");
    }
    @After
    public void cleanUp() {
        bankService = null;
    }

    @Test
    public void testChangeBalanceInRUB() throws Exception {
        int rubleBalance = 100;
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(rubleBalance, Currency.RUB);

        Assert.assertEquals(rubleBalance, bankService.getClientByPhone("+7").getClientBalances().getBalanceByCurrency(Currency.RUB));
    }

    @Test
    public void testChangeBalanceInUSD() throws Exception {
        int usdBalance = 70;
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(usdBalance, Currency.USD);

        Assert.assertEquals(usdBalance, bankService.getClientByPhone("+7").getClientBalances().getBalanceByCurrency(Currency.USD));
    }

    @Test
    public void testChangeBalanceInEUR() throws Exception {
        int euroBalance = 50;
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(euroBalance, Currency.EUR);

        Assert.assertEquals(euroBalance, bankService.getClientByPhone("+7").getClientBalances().getBalanceByCurrency(Currency.EUR));
    }

    @Test
    public void testTransactionDate() throws Exception {
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(50);
        List<TransactionData> listOfTransactions = bankService.getClientByPhone("+7").getClientBalances().getTransactionList();
        TransactionData lastTransaction = listOfTransactions.get(listOfTransactions.size() - 1);
        Date date = lastTransaction.getDate();
        Assert.assertNotNull(date);
    }

    @Test
    public void testCreateNewClient() {
        Assert.assertTrue(bankService.clientExists("+7"));
    }

    @Test
    public void testDefaultBalance() {
        assertEquals(0, bankService.getClientByPhone("+7").getBalance());
    }

    @Test
    public void testBalanceIncrement() throws Exception {
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(100);
        Assert.assertEquals(100, bankService.getClientByPhone("+7").getBalance());
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception {
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(100);
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(-20);
        assertEquals(80, bankService.getClientByPhone("+7").getClientBalances().getBalance());
    }

    @Test(expected = Exception.class)
    public void testOverdraft() throws Exception {
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(200);
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(-300);
    }

    @Test
    public void testAccountStatement() throws Exception {
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(200);
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(-100);
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(50);
        List<Integer> transactionValues = getTransactionValues(bankService.getClientByPhone("+7").getClientBalances().getTransactionList());
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
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(100);
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(-50);
        bankService.getClientByPhone("+79").getClientBalances().changeBalance(200);
        bankService.getClientByPhone("+79").getClientBalances().changeBalance(30);

        List<Integer> transactionValues = getTransactionValues(bankService.getClientByPhone("+7").getClientBalances().getTransactionList());
        Assert.assertEquals(Arrays.asList(100, -50), transactionValues);
    }

    @Test
    public void testWhenBalanceOf1stClientIncreasesBy100TheBalanceOfSecondIs0() throws Exception {
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(100);
        Assert.assertEquals(0, bankService.getClientByPhone("+79").getBalance());
    }

    @Test
    public void testFirstClientBalanceAfterTransferringMoney() throws Exception {
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(100);
        bankService.transferMoney("+7", "+79", 30);
        Assert.assertEquals(70, bankService.getClientByPhone("+7").getBalance());
    }

    @Test
    public void testSecondClientBalanceAfterReceivingMoney() throws Exception {
        bankService.getClientByPhone("+7").getClientBalances().changeBalance(100);
        bankService.transferMoney("+7", "+79", 30);
        Assert.assertEquals(30, bankService.getClientByPhone("+79").getBalance());
    }
}