package web;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static java.net.http.HttpRequest.BodyPublishers.noBody;

public class AcceptanceTest {

    private int checkHead(String urlInputString) throws IOException, InterruptedException, URISyntaxException {
        return sendRequest(HeadRequest(urlInputString)).statusCode();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest HeadRequest(String urlInputString) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .method("HEAD", noBody())
                .build();
    }

    private HttpRequest postRequest(String urlInputString) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(urlInputString))
                .POST(noBody())
                .build();
    }

    @Test
    public void testLocalHost() throws IOException, URISyntaxException, InterruptedException {
        Assert.assertEquals(404, checkHead("http://localhost:8080/bank/v1/clients/2406846c-3c01-4297-b8c2-8a960ddefce6"));
    }

    @Test
    public void postClientShouldReturn201() throws IOException, URISyntaxException, InterruptedException {
        final HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));

        Assert.assertEquals(201, response.statusCode());
    }

    @Test
    public void headClient() throws IOException, URISyntaxException, InterruptedException {
        final HttpResponse<String> response = sendRequest(postRequest("http://localhost:8080/bank/v1/clients/"));

        Assert.assertEquals(200, checkHead("http://localhost:8080/bank/v1/clients/" + response.body()));
    }

    @Test
    public void postClientThatIsNotExist() throws IOException, URISyntaxException, InterruptedException {
        Assert.assertEquals(404, checkHead("http://localhost:8080/bank/v1/clients/" + UUID.randomUUID()));
    }

/*
    GET
    ClientDto : {
        balance : 100
    }

    POST
    http://localhost:8080/bank/v1/clients/124i01982509182059/transactions
    {money : 200}

    GET
    http://localhost:8080/bank/v1/clients/124i01982509182059/transactions
    [
    {money : 200, date: }
    {money : 2200}
    {money : -00}
    {money : 200}
    ]

 */

}
