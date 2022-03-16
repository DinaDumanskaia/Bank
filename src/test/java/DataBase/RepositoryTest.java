package DataBase;

import bank.domain.Client;
import bank.application.ClientNotFoundException;
import bank.application.ClientRepository;
import bank.infrastructure.database.RealClientRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class RepositoryTest {
    ClientRepository repository = new RealClientRepository();

    @Test
    public void testClientDoesNotExists() {
        Assert.assertFalse(repository.clientExists(UUID.randomUUID()));
    }

    @Test(expected = ClientNotFoundException.class)
    public void testGetNotExistingClient() {
        repository.getClientById(UUID.randomUUID());
    }

    @Test
    public void testClientEqualsRepositoryClient() {
        Client client = new Client();
        repository.saveClient(client);
        Client returnedClient = repository.getClientById(client.getID());
        Assert.assertEquals(client, returnedClient);
    }

    @Test
    public void testExistsTrueAfterCreation() {
        Client client = new Client();
        repository.saveClient(client);
        Assert.assertTrue(repository.clientExists(client.getID()));
    }

    @Test
    public void testClientEqualsRepositoryClient2() {
        Client returnedClient = repository.getClientById(UUID.fromString("bcc9372b-41ee-4efa-804d-1474e14777f1"));
        System.out.println(returnedClient);
    }


}
