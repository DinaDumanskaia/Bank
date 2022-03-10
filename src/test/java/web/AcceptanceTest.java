package web;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class AcceptanceTest {
    UUID uuid;
    URL url;

    private BufferedReader getBufferedReader(String urlString) throws IOException {
        url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(20);
        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    private int checkHead(String urlInputString) throws IOException {
        URL url = new URL(urlInputString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        int status = connection.getResponseCode();
        return status;
    }

    @Test
    public void testLocalHost() throws IOException {
        Assert.assertEquals(500, checkHead("http://localhost:8080/bank/v1"));
        Assert.assertEquals(404, checkHead("http://localhost:8080/bank/v1/clients/2406846c-3c01-4297-b8c2-8a960ddefce6"));
    }

    @Test
    public void testPostNewClient() throws Exception {
        Assert.assertEquals(200, checkHead("http://localhost:8080/bank/v1/clients/" + Controller.createClient()));
    }

    @Test
    public void testNewClientsBalanceIsEmpty() throws Exception {
        Assert.assertEquals(0, checkHead("http://localhost:8080/bank/v1/clients/" + Controller.createClient() + "/balance"));
    }

}
