package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.event.xml.schema.DevicePatient;
import com.scnsoft.eldermark.event.xml.schema.Patient;

import java.util.List;

public interface EventClientService {

    List<Client> getOrCreateClient(Community community, Patient patient);

    List<Client> getOrCreateClient(Community community, DevicePatient patient);
}
