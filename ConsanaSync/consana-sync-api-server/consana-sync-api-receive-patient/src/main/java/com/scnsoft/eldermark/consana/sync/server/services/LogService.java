package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.log.ConsanaBaseLog;

public interface LogService {

    void saveInNewTransaction(ConsanaBaseLog log);
}
