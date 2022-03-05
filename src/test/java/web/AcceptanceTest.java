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

    @Test
    public void testGettingBalanceForNonExistentClient() throws IOException {

        url = new URL("http://localhost:8080/get_balance?id=7&currency=rub");
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(20);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        Assert.assertEquals("Balance couldn't be returned for the client", bufferedReader.readLine());
    }

    @Test
    public void testIsClientExists() throws IOException {
        url = new URL("http://localhost:8080/client_exists?id=7");
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(20);
        BufferedReader bufferedReader = new BufferedReader((new InputStreamReader((conn.getInputStream()))));
        Assert.assertEquals("Client not found", bufferedReader.readLine());
    }
}
