import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Tests {
    @Test
    public void testClientCreation() {
        BankClient client = new BankClient();
        assertNotNull(client);
    }

    @Test
    public void testDefaultBalance() {
        BankClient client = new BankClient();
        assertEquals(client.getBalance(), 0, 0.001);
    }

    @Test
    public void testBalanceIncrement() throws Exception {
        BankClient client = new BankClient();
        client.changeBalance(100);
        assertEquals(client.getBalance(), 100, 0.001);
    }

    @Test
    public void testBalanceIncr100Decr20() throws Exception {
        BankClient client = new BankClient();
        client.changeBalance(100);
        client.changeBalance(-20);
        assertEquals(client.getBalance(), 80, 0.001);
    }

    @Test (expected = Exception.class)
    public void testOverdraft() throws Exception {
        BankClient client = new BankClient();
        client.changeBalance(200);
        client.changeBalance(-300);
    }

    @Test
    public void testAccountStatement() throws Exception {
        BankClient client = new BankClient();
        client.changeBalance(200.0);
        client.changeBalance(-100.0);
        client.changeBalance(50.0);
        Assert.assertThat(client.getAccountStatement(), CoreMatchers.hasItems(200.0, -100.0, 50.0));
    }
}