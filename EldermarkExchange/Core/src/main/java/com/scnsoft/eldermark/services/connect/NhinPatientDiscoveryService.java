package com.scnsoft.eldermark.services.connect;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.ResidentFilter;

import java.util.List;

public interface NhinPatientDiscoveryService {
    public List<ResidentDto> patientDiscovery(ResidentFilter filter, String assigningAuthorityId, ExchangeUserDetails exchangeUserDetails);
}
