package bank.Database;

import bank.Client;

import java.util.UUID;

public interface ClientRepository {
    boolean clientExists(UUID clientId);

    Client getClientById(UUID id);

    void saveClient(Client client);
}
