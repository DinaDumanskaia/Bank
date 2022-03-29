package bank.integration;

import bank.application.exceptions.IllegalClientIdException;
import bank.domain.Client;
import bank.application.exceptions.ClientNotFoundException;
import bank.application.adapters.ClientRepository;
import bank.domain.Currency;
import bank.infrastructure.database.RealClientRepository;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class RepositoryTest {
    ClientRepository repository = new RealClientRepository();

    public RepositoryTest() throws SQLException {
    }

    @Test
    public void testClientDoesNotExists() {
        Assert.assertFalse(repository.clientExists(UUID.randomUUID()));
    }

    @Test(expected = ClientNotFoundException.class)
    public void testGetNotExistingClient() throws IllegalClientIdException {
        repository.getClientById(UUID.randomUUID());
    }

    @Test
    public void testClientEqualsRepositoryClient() throws IllegalClientIdException {
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
    public void testClientHasTwoTransactions() throws IllegalClientIdException {
        Client client = new Client();
        client.changeBalance(200, Currency.RUB, new Date());
        client.changeBalance(50, Currency.RUB, new Date());
        repository.saveClient(client);
        Client returnedClient = repository.getClientById(client.getID());
        Assert.assertEquals(client, returnedClient);
    }

    @Test
    public void testEmptyClient() throws IllegalClientIdException {
        Client client = new Client();
        repository.saveClient(client);

        Client returnedClient = repository.getClientById(client.getID());
        returnedClient.changeBalance(200, Currency.RUB, new Date());
        repository.saveClient(returnedClient);

        Client returnedClientAfterChange = repository.getClientById(client.getID());
        Assert.assertEquals(200, returnedClientAfterChange.getBalance());
    }

    @Test
    public void secondTransactionShouldBeDifferent() throws IllegalClientIdException {
        Client client = new Client();
        repository.saveClient(client);
        client.changeBalance(10, Currency.RUB, new Date());
        repository.saveClient(client);
        client.changeBalance(500, Currency.RUB, new Date());
        repository.saveClient(client);
        Client returnedClient = repository.getClientById(client.getID());
        Assert.assertEquals(510, returnedClient.getBalance());
    }


}
