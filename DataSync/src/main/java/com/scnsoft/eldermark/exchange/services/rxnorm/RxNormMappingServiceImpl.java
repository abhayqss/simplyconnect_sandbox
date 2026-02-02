package com.scnsoft.eldermark.exchange.services.rxnorm;

import com.scnsoft.eldermark.exchange.dao.rxnorm.RxNormMappingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RxNormMappingServiceImpl implements RxNormMappingService {
    @Autowired
    private RxNormMappingDao dao;

    @Override
    //@Transactional("rxnormDatabaseTransactionManager", readOnly = true) - Transaction isn't necessary here
    public String getRxNormCode(String ndc) {
        return dao.getRxNormCode(ndc);
    }
}
