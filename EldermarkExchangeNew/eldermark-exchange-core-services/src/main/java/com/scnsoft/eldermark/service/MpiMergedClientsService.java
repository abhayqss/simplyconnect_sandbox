package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MpiMergedClients;

public interface MpiMergedClientsService {

    MpiMergedClients mergeClients(Client client1, Client client2);

    MpiMergedClients mergeClients(Client client1, Client client2, Double confidence);

}
