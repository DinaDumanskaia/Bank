package bank.application.adapters;

import bank.application.exceptions.IllegalClientIdException;
import bank.domain.Client;

import java.util.UUID;

public interface ClientRepository {
    boolean clientExists(UUID clientId);

    Client getClientById(UUID id) throws IllegalClientIdException;

    void saveClient(Client client);

}
