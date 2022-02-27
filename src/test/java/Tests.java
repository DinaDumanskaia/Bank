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
    //разные валюты:
    //клиент может пополнять/снимать со счета деньги в разных валютах
    public void testChangeBalanceInDifferentCurrencies() throws Exception {
        //кладу на счет сумму в рублях, долларах и евро
        //проверяю, что существует сумма в рублях, долларах и евро
        int rubleBalance = 100;
        bankService.changeBalance("+7", rubleBalance, Currency.RUB);
//        bankService.changeBalance("+7", 50, USD);
//        bankService.changeBalance("+7", 30, EUR);

        Assert.assertEquals(rubleBalance, bankService.getBalance("+7", Currency.RUB));
    }

    @Test
    public void testTransactionDate() throws Exception {
        bankService.changeBalance("+7", 50);
        List<TransactionData> listOfTransactions = bankService.getAccountStatement("+7");
        TransactionData lastTransaction = listOfTransactions.get(listOfTransactions.size() - 1);
        Date date = lastTransaction.getDate();
        Assert.assertNotNull(date);
    }


    @Test
    public void testCreateThreeClients() throws Exception {
        bankService.createNewClient("+8");
        bankService.createNewClient("+9");
        bankService.createNewClient("+1");
        bankService.changeBalance("+8", 8);
        bankService.changeBalance("+9", 9);
        bankService.changeBalance("+1", 1);
        Assert.assertNotEquals(bankService.getBalance("+8"), bankService.getBalance("+9"));
        Assert.assertNotEquals(bankService.getBalance("+8"), bankService.getBalance("+1"));
        Assert.assertNotEquals(bankService.getBalance("+1"), bankService.getBalance("+9"));
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
        Assert.assertEquals(bankService.getBalance("+7"), 100);
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception {
        bankService.changeBalance("+7",100);
        bankService.changeBalance("+7", -20);
        assertEquals(bankService.getBalance("+7"), 80, 0.001);
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
        List<Integer> transactionValues = getTransactionValues(bankService.getAccountStatement("+7"));
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

        List<Integer> transactionValues = getTransactionValues(bankService.getAccountStatement("+7"));
        Assert.assertEquals(Arrays.asList(100, -50), transactionValues);
    }

    @Test
    public void testWhenBalanceOf1stClientIncreasesBy100TheBalanceOfSecondIs0() throws Exception {
        bankService.changeBalance("+7",100);
        Assert.assertEquals(0, bankService.getBalance("+79"));
    }

    @Test
    public void testWhenBalanceOfFirstClIncrBy100FirstClTransfer30ToSecondClTheBalanceOfFirstIs70() throws Exception {
        bankService.changeBalance("+7",100);
        bankService.transferMoney("+7", "+79", 30);
        Assert.assertEquals(70, bankService.getBalance("+7"));
    }

    @Test
    public void testWhenBalanceOfFirstClIncrBy100FirstClTransfer30ToSecondClTheBalanceOfSecondIs30() throws Exception {
        bankService.changeBalance("+7",100);
        bankService.transferMoney("+7", "+79", 30);
        Assert.assertEquals(30, bankService.getBalance("+79"));
    }
}