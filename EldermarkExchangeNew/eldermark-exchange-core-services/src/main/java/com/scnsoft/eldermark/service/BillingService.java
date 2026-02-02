package com.scnsoft.eldermark.service;

import java.util.List;
import java.util.Optional;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.client.ClientHealthPlan;

public interface BillingService {

    List<ClientHealthPlan> findByClientId(Long clientId);
    
    Optional<Client> findOptionalById(Long clientId);
}
