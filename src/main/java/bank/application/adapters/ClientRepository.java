package bank.application.adapters;

import bank.domain.Client;

import java.util.UUID;

public interface ClientRepository {
    boolean clientExists(UUID clientId);

    Client getClientById(UUID id);

    void saveClient(Client client);

}
