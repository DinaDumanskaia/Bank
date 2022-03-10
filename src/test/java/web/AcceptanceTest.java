package web;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AcceptanceTest {

    private int checkHead2(String urlInputString) throws IOException, InterruptedException, URISyntaxException {
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
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
    }

    @Test
    public void testLocalHost() throws IOException, URISyntaxException, InterruptedException {
        Assert.assertEquals(500, checkHead2("http://localhost:8080/bank/v1"));
        Assert.assertEquals(404, checkHead2("http://localhost:8080/bank/v1/clients/2406846c-3c01-4297-b8c2-8a960ddefce6"));
    }

}
