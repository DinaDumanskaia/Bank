import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

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
        Assert.assertEquals(Arrays.asList(200, -100, 50), bankService.getAccountStatement("+7"));
    }

    @Test
    public void testAccountStatement2() throws Exception {
        bankService.changeBalance("+7", 100);
        bankService.changeBalance("+7", -50);
        bankService.changeBalance("+79", 200);
        bankService.changeBalance("+79", 30);

        Assert.assertEquals(Arrays.asList(100, -50), bankService.getAccountStatement("+7"));
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