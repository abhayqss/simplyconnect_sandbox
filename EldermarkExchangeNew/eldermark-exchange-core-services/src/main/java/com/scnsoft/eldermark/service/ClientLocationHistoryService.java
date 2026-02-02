package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.ClientLocationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientLocationHistoryService extends ProjectingService<Long> {

    <P> Page<P> findByClientId(Long clientId, Class<P> projection, Pageable pageable);

    ClientLocationHistory save(ClientLocationHistory clientLocationHistory);

}
