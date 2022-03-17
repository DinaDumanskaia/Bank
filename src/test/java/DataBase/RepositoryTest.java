package DataBase;

import bank.domain.Client;
import bank.application.ClientNotFoundException;
import bank.application.ClientRepository;
import bank.domain.Currency;
import bank.infrastructure.database.RealClientRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
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
        client.changeBalance(200, Currency.RUB, new Date());
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

    @Test
    public void testClientHasTwoTransactions() {
        Client client = new Client();
        client.changeBalance(200, Currency.RUB, new Date());
        client.changeBalance(50, Currency.RUB, new Date());
        repository.saveClient(client);
        Client returnedClient = repository.getClientById(client.getID());
        Assert.assertEquals(client, returnedClient);
    }

    @Test
    public void testEmptyClient() {
        Client client = new Client();
        repository.saveClient(client);

        Client returnedClient = repository.getClientById(client.getID());
        returnedClient.changeBalance(200, Currency.RUB, new Date());
        repository.saveClient(returnedClient);

        Client returnedClientAfterChange = repository.getClientById(client.getID());
        Assert.assertEquals(200, returnedClientAfterChange.getBalance());
    }

    @Test
    public void secondTransactionShouldBeDifferent() {
        Client client = new Client();
        client.changeBalance(10, Currency.RUB, new Date());
        repository.saveClient(client);
        client.changeBalance(500, Currency.RUB, new Date());
        repository.saveClient(client);
        Client returnedClient = repository.getClientById(client.getID());
        Assert.assertEquals(510, returnedClient.getBalance());
    }


}
