import org.junit.After;
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
    @After
    public void cleanUp() {
        bankService = null;
    }

    @Test
    public void testChangeBalanceInRUB() throws Exception {
        int rubleBalance = 100;
        bankService.getClientByPhone("+7").getMoneyAccount(Currency.RUB).changeBalance(rubleBalance);

        Assert.assertEquals(rubleBalance, bankService.getClientByPhone("+7").getMoneyAccount(Currency.RUB).getBalance());
    }

    @Test
    public void testChangeBalanceInUSD() throws Exception {
        int usdBalance = 70;
        bankService.getClientByPhone("+7").getMoneyAccount(Currency.USD).changeBalance(usdBalance);

        Assert.assertEquals(usdBalance, bankService.getClientByPhone("+7").getMoneyAccount(Currency.USD).getBalance());
    }

    @Test
    public void testChangeBalanceInEUR() throws Exception {
        int euroBalance = 50;
        bankService.getClientByPhone("+7").getMoneyAccount(Currency.EUR).changeBalance(euroBalance);

        Assert.assertEquals(euroBalance, bankService.getClientByPhone("+7").getMoneyAccount(Currency.EUR).getBalance());
    }

    @Test
    public void testTransactionDate() throws Exception {
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(50);
        List<TransactionData> listOfTransactions = bankService.getClientByPhone("+7").getMoneyAccount().getTransactionList();
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
        assertEquals(0, bankService.getClientByPhone("+7").getMoneyAccount().getBalance());
    }

    @Test
    public void testBalanceIncrement() throws Exception {
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(100);
        Assert.assertEquals(100, bankService.getClientByPhone("+7").getMoneyAccount().getBalance());
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception {
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(100);
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(-20);
        assertEquals(80, bankService.getClientByPhone("+7").getMoneyAccount().getBalance());
    }

    @Test(expected = Exception.class)
    public void testOverdraft() throws Exception {
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(200);
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(-300);
    }

    @Test
    public void testAccountStatement() throws Exception {
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(200);
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(-100);
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(50);
        List<Integer> transactionValues = getTransactionValues(bankService.getClientByPhone("+7").getMoneyAccount().getTransactionList());
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
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(100);
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(-50);
        bankService.getClientByPhone("+79").getMoneyAccount().changeBalance(200);
        bankService.getClientByPhone("+79").getMoneyAccount().changeBalance(30);

        List<Integer> transactionValues = getTransactionValues(bankService.getClientByPhone("+7").getMoneyAccount().getTransactionList());
        Assert.assertEquals(Arrays.asList(100, -50), transactionValues);
    }

    @Test
    public void testWhenBalanceOf1stClientIncreasesBy100TheBalanceOfSecondIs0() throws Exception {
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(100);
        Assert.assertEquals(0, bankService.getClientByPhone("+79").getMoneyAccount().getBalance());
    }

    @Test
    public void testFirstClientBalanceAfterTransferringMoney() throws Exception {
        bankService.getClientByPhone("+7").getMoneyAccount().changeBalance(100);
        bankService.transferMoney("+7", "+79", 30);
        Assert.assertEquals(70, bankService.getClientByPhone("+7").getMoneyAccount().getBalance());
    }

    @Test
    public void testSecondClientBalanceAfterReceivingMoney() throws Exception {
        Client client = bankService.getClientByPhone("+7");
        MoneyAccount moneyAccount = client.getMoneyAccount();
        moneyAccount.changeBalance(100);

        bankService.transferMoney("+7", "+79", 30);
        Assert.assertEquals(30, bankService.getClientByPhone("+79").getMoneyAccount().getBalance());
    }
}