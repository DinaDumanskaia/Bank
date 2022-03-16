package DataBase;

import bank.Client;
import bank.ClientNotFoundException;
import bank.FakeClientRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class RepositoryTest {
    FakeClientRepository repository = new FakeClientRepository();

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

}
