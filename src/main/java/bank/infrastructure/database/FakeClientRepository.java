package bank.infrastructure.database;

import bank.domain.Client;
import bank.application.ClientNotFoundException;
import bank.application.ClientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeClientRepository implements ClientRepository {
    private final List<Client> bankClients = new ArrayList<>();

    @Override
    public boolean clientExists(UUID clientId) {
        return bankClients.stream()
                .anyMatch(client -> client.getID().equals(clientId));
    }

    @Override
    public Client getClientById(UUID id) {
        return bankClients.stream()
                .filter(client -> client.getID().equals(id))
                .findFirst()
                .orElseThrow(() -> new ClientNotFoundException("bank.domain.Client not found"));
    }

    @Override
    public void saveClient(Client client) {
        bankClients.add(client);
    }
}
