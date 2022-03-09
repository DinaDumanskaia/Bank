package web;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AcceptanceTest {
    URL url;

    private BufferedReader getBufferedReader(String urlString) throws IOException {
        url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(20);
        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    @Test
    public void testGettingBalanceForNonExistentClient() throws IOException {
        Assert.assertEquals("Balance couldn't be returned for the client", getBufferedReader("http://localhost:8080/get_balance?id=7&currency=rub").readLine());
    }

    @Test
    public void testIsClientExists() throws IOException {
        Assert.assertEquals("Client not found", getBufferedReader("http://localhost:8080/client_exists?id=7").readLine());
    }
}
