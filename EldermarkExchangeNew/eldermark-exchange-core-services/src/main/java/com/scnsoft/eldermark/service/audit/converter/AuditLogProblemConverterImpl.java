package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.security.projection.entity.ProblemNameAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.ClientProblemService;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class AuditLogProblemConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> problemActivitiesWithNote = List.of(
            AuditLogActivity.PROBLEM_VIEW
    );

    @Autowired
    private ClientProblemService clientProblemService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (problemActivitiesWithNote.contains(activity)) {
                var problemAware = clientProblemService.findById(relatedId, ProblemNameAware.class);
                var problem = problemAware.getProblem();
                return List.of("Problem: " + problem);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.PROBLEM;
    }
}
