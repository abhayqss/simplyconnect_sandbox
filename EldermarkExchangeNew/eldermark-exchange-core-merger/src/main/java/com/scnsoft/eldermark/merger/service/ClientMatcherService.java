package com.scnsoft.eldermark.merger.service;

import com.scnsoft.eldermark.entity.Client;

import java.util.Collection;
import java.util.function.Supplier;

public interface ClientMatcherService {
    MatchResult<Client> findMatchedPatients(Client targetClient, Supplier<Collection<Client>> sourceClients, boolean respectHieConsentPolicy);

    MatchResult<Client> findMatchedPatients(Collection<Client> targetClients, Supplier<Collection<Client>> sourceClients, boolean respectHieConsentPolicy);
}

