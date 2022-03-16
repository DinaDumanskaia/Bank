package bank.infrastructure.database;

import bank.domain.Client;
import bank.application.ClientNotFoundException;
import bank.application.ClientRepository;

import java.util.UUID;

public class RealClientRepository implements ClientRepository {

    @Override
    public boolean clientExists(UUID clientId) {
        return false;
    }

    @Override
    public Client getClientById(UUID id) {
        throw new ClientNotFoundException("Client not found");
    }

    @Override
    public void saveClient(Client client) {

    }
}
