package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.shared.marketplace.InNetworkInsuranceDto;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface InNetworkInsuranceService {
    InNetworkInsuranceDto findById (Long id);
    List<InNetworkInsuranceDto> findByIds(List<Long> ids);
}
