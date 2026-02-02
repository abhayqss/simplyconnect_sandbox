package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.healthpartners.HealthPartnersTestOutcome;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersFileLog;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.service.inbound.healthpartners.HpTestFileGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthPartnersFacadeImpl implements HealthPartnersFacade {

    @Autowired
    private HpTestFileGenerator hpTestFileGenerator;

    @Override
    @Transactional(readOnly = true)
    public HealthPartnersTestOutcome submitTestCSV(String csv, HpFileType fileType) {
        var fileLog = hpTestFileGenerator.writeTestFile(csv, fileType);
        return convert(fileLog);
    }

    private HealthPartnersTestOutcome convert(HealthPartnersFileLog fileLog) {
        if (fileLog == null) {
            return null;
        }
        var result = new HealthPartnersTestOutcome();
        result.setSuccess(fileLog.isSuccess());
        result.setTestFileName(fileLog.getFileName());
        result.setProcessingErrorMessage(fileLog.getErrorMessage());

        return result;
    }
}
