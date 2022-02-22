import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class Tests {

    @Test
    public void testCreateNewClient() {
        BankService bankService = new BankService();
        bankService.createNewClient("+7");
        Assert.assertTrue(bankService.clientExists("+7"));
    }

    @Test
    public void testDefaultBalance() {
        BankService bankService = new BankService();
        bankService.createNewClient("+7");
        assertEquals(0, bankService.getBalance("+7"));
    }

    @Test
    public void testBalanceIncrement() throws Exception {
        BankService bankService = new BankService();
        bankService.createNewClient("+7");
        bankService.changeBalance("+7", 100);
        Assert.assertEquals(bankService.getBalance("+7"), 100);
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception {
        BankService bankService = new BankService();
        bankService.createNewClient("+7");
        bankService.changeBalance("+7",100);
        bankService.changeBalance("+7", -20);
        assertEquals(bankService.getBalance("+7"), 80, 0.001);
    }

    @Test(expected = Exception.class)
    public void testOverdraft() throws Exception {
        BankService client = new BankService();
        client.changeBalance("+7", 200);
        client.changeBalance("+7", -300);
    }

    @Test
    public void testAccountStatement() throws Exception {
        BankService bankService = new BankService();
        bankService.createNewClient("+7");
        bankService.changeBalance("+7", 200);
        bankService.changeBalance("+7", -100);
        bankService.changeBalance("+7", 50);
        Assert.assertEquals(Arrays.asList(200, -100, 50), bankService.getAccountStatement("+7"));
    }
}