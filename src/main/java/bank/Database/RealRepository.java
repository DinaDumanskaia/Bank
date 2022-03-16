package bank.Database;

import bank.Client;
import bank.ClientNotFoundException;

import java.util.UUID;

public class RealRepository implements ClientRepository {

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
