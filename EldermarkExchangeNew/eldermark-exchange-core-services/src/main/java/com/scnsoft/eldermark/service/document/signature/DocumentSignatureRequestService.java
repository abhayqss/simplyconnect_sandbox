package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureRequest;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestPdcFlowCallbackLog;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.service.ProjectingService;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DocumentSignatureRequestService extends ProjectingService<Long> {

    DocumentSignatureRequest findById(Long id);

    Optional<DocumentSignatureRequest> findByPdcflowSignatureId(BigInteger pdcFlowSignatureId);

    List<DocumentSignatureRequest> submitRequests(List<SubmitTemplateSignatureRequest> dtos);

    void cancelRequest(Long id, Employee currentEmployee);

    DocumentSignatureRequest renewRequest(Long requestId, Instant newExpirationDate, Employee currentEmployee);

    DocumentSignatureRequest renewRequest(DocumentSignatureRequest request, Instant newExpirationDate, Employee currentEmployee);

    boolean canRenewByStatus(DocumentSignatureRequestStatus status);

    List<CareTeamRole> getAllowedRecipientRoles();

    List<IdNamesAware> getAllowedRecipientEmployees(Long clientId, Long documentId);

    void processStatusUpdateCallback(DocumentSignatureRequest request, DocumentSignatureRequestPdcFlowCallbackLog logEntry);

    DocumentSignatureRequestNotification resendPin(Long requestId);

    Long countByOrganizationIdAndStatuses(Long organizationId, List<DocumentSignatureRequestStatus> statuses);

    Long countByCommunityIdAndStatuses(Long communityId, List<DocumentSignatureRequestStatus> statuses);

    List<IdAware> findAllRequestedIdsByOrganizationId(Long organizationId);

    void cancelRequestedByOrganizationIdAsync(Long organizationId, Long employeeId);

    void cancelRequestedByCommunityIdAsync(Long organizationId, Long employeeId);

    List<DocumentSignatureRequest> findAllByBulkRequestIdAndStatusIn(Long bulkRequestId, Collection<DocumentSignatureRequestStatus> statuses);

    List<DocumentSignatureRequest> findAllByBulkRequestIdAndSignatureTemplateIdAndStatusIn(Long bulkRequestId, Long templateId, Collection<DocumentSignatureRequestStatus> statuses);

    DocumentSignatureRequest save(DocumentSignatureRequest request);

    <P> List<P> findAllByBulkRequestId(Long bulkRequestId, Class<P> projectionClass);

    void cancelRequestsForOnHoldCtmByClientIdAsync(Long clientId, Long currentEmployeeId);

    void cancelRequestsForOnHoldCtmByCommunityIdAsync(Long communityId, Long currentEmployeeId);
}
