package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class AuditLogRecordSearchConverterImpl implements AuditLogBaseConverter<Long> {

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.RECORD_SEARCH;
    }
}
