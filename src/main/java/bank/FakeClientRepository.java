package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeClientRepository {
    private final List<Client> bankClients = new ArrayList<>();

    public boolean clientExists(UUID clientId) {
        return bankClients.stream()
                .anyMatch(client -> client.getID().equals(clientId));
    }

    public Client getClientById(UUID id) {
        return bankClients.stream()
                .filter(client -> client.getID().equals(id))
                .findFirst()
                .orElseThrow(() -> new ClientNotFoundException("bank.Client not found"));
    }

    public void saveClient(Client client) {
        bankClients.add(client);
    }
}
