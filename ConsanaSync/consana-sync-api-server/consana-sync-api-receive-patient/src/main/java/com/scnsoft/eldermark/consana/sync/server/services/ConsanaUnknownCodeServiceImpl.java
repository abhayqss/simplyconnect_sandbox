package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.dao.ConsanaUnknownCodeDao;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ConsanaUnknownCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(noRollbackFor = Exception.class)
public class ConsanaUnknownCodeServiceImpl implements ConsanaUnknownCodeService {

    @Autowired
    private ConsanaUnknownCodeDao consanaUnknownCodeDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ConsanaUnknownCode saveCode(ConsanaUnknownCode code) {
        return consanaUnknownCodeDao.save(code);
    }
}
