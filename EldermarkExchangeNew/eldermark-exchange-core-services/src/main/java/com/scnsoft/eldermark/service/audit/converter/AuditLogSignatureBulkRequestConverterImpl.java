package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.projection.SignatureRequestTemplateNameAware;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class AuditLogSignatureBulkRequestConverterImpl extends AuditLogBaseConverterImpl implements AuditLogBaseConverter<Long> {

    private static final List<AuditLogActivity> signatureBulkRequestActivitiesWithNote = List.of(
            AuditLogActivity.SIGNATURE_BULK_REQUEST_SUBMIT,
            AuditLogActivity.SIGNATURE_BULK_REQUEST_CANCEL,
            AuditLogActivity.SIGNATURE_BULK_REQUEST_RESUBMIT
    );

    @Autowired
    private DocumentSignatureRequestService signatureRequestService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<Long> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (auditLog.isMobile()) {
                if (!AuditLogActivity.SIGNATURE_BULK_REQUEST_SUBMIT.equals(activity) && signatureBulkRequestActivitiesWithNote.contains(activity)) {
                    var templatesWithRecipients = collectRecipientsByTemplateName(relatedId);
                    return List.of(templatesWithRecipients + "\nMobile app");
                }
                return List.of("Mobile app");
            }

            if (signatureBulkRequestActivitiesWithNote.contains(activity)) {
                return collectRecipientsByTemplateName(relatedId);
            }
        }

        return Collections.emptyList();
    }

    private List<String> collectRecipientsByTemplateName(Long relatedId) {
        var signatureAwares =
                signatureRequestService.findAllByBulkRequestId(relatedId, SignatureRequestTemplateNameAware.class);

        var templateSignatureAwareMap = signatureAwares.stream()
                .collect(Collectors.groupingBy(SignatureRequestTemplateNameAware::getSignatureTemplateId));

        var templatesMap = signatureAwares.stream()
                .collect(StreamUtils.toMapOfUniqueKeysAndThen(
                        SignatureRequestTemplateNameAware::getSignatureTemplateId,
                        SignatureRequestTemplateNameAware::getSignatureTemplateTitle)
                );

        return templatesMap.entrySet().stream()
                .map(entry -> entry.getValue() + "\n" +
                        templateSignatureAwareMap.get(entry.getKey()).stream()
                                .map(this::resolveNameAndRole)
                                .collect(Collectors.joining(", ")) + "\n"
                )
                .collect(Collectors.toList());
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SIGNATURE_BULK_REQUEST;
    }
}
