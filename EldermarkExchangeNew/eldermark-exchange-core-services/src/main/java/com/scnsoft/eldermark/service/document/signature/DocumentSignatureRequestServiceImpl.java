package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.dao.signature.DocumentSignatureRequestDao;
import com.scnsoft.eldermark.dao.specification.DocumentSignatureRequestSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureRequest;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.document.ClientDocumentUploadData;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.SharingOption;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventAuthor;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestPdcFlowCallbackLog;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.PdcFlowOverlayBoxType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.CareTeamRoleService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.EventTypeService;
import com.scnsoft.eldermark.service.document.DocumentFileService;
import com.scnsoft.eldermark.service.document.DocumentService;
import com.scnsoft.eldermark.service.document.UploadClientDocumentService;
import com.scnsoft.eldermark.service.document.signature.notification.SignatureCancelNotificationService;
import com.scnsoft.eldermark.service.document.signature.notification.SignatureNotificationService;
import com.scnsoft.eldermark.service.document.signature.provider.DocumentSignatureProvider;
import com.scnsoft.eldermark.service.document.signature.provider.SignatureAlreadyCanceledProviderException;
import com.scnsoft.eldermark.service.storage.SignatureHistoryFileStorage;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureRequestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
public class DocumentSignatureRequestServiceImpl implements DocumentSignatureRequestService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentSignatureRequestServiceImpl.class);
    private static final ThreadLocal<SecureRandom> random = ThreadLocal.withInitial(SecureRandom::new);
    private static final int MAX_PIN = 99999999;
    private static final int MIN_PIN = 1000; //PDCFlow doesn't allow leading zeroes in pin (like 0123)
    private final static List<CareTeamRoleCode> RECIPIENT_ALLOWED_ROLES = List.of(
            CareTeamRoleCode.ROLE_PARENT_GUARDIAN,
            CareTeamRoleCode.ROLE_CASE_MANAGER,
            CareTeamRoleCode.ROLE_CARE_COORDINATOR,
            CareTeamRoleCode.ROLE_PRIMARY_PHYSICIAN,
            CareTeamRoleCode.ROLE_PHARMACIST,
            CareTeamRoleCode.ROLE_PHARMACY_TECHNICIAN,
            CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH,
            CareTeamRoleCode.ROLE_COMMUNITY_MEMBERS,
            CareTeamRoleCode.ROLE_SERVICE_PROVIDER,
            CareTeamRoleCode.ROLE_NURSE,
            CareTeamRoleCode.ROLE_TELE_HEALTH_NURSE,
            CareTeamRoleCode.ROLE_ADMINISTRATOR,
            CareTeamRoleCode.ROLE_COMMUNITY_ADMINISTRATOR
    );
    private static final Map<DocumentSignatureRequestStatus, Set<DocumentSignatureRequestStatus>> VALID_STATUS_TRANSITIONS =
            Map.ofEntries(
                    entry(DocumentSignatureRequestStatus.CREATED,
                            EnumSet.of(
                                    DocumentSignatureRequestStatus.SIGNATURE_REQUESTED,
                                    DocumentSignatureRequestStatus.REVIEW_REQUESTED,
                                    DocumentSignatureRequestStatus.REQUEST_FAILED
                            )
                    ),

                    entry(DocumentSignatureRequestStatus.REVIEW_REQUESTED,
                            EnumSet.of(
                                    DocumentSignatureRequestStatus.REVIEWED,
                                    DocumentSignatureRequestStatus.SIGNATURE_FAILED,
                                    DocumentSignatureRequestStatus.CANCELED
                            )
                    ),

                    entry(DocumentSignatureRequestStatus.SIGNATURE_REQUESTED,
                            EnumSet.of(
                                    DocumentSignatureRequestStatus.SIGNED,
                                    DocumentSignatureRequestStatus.SIGNATURE_FAILED,
                                    DocumentSignatureRequestStatus.CANCELED
                            )
                    ),

                    entry(DocumentSignatureRequestStatus.REQUEST_FAILED,
                            Collections.emptySet()
                    ),

                    entry(DocumentSignatureRequestStatus.SIGNED,
                            Collections.emptySet()
                    ),

                    entry(DocumentSignatureRequestStatus.SIGNATURE_FAILED,
                            Collections.emptySet()
                    ),

                    entry(DocumentSignatureRequestStatus.EXPIRED,
                            Collections.emptySet()
                    ),

                    entry(DocumentSignatureRequestStatus.CANCELED,
                            Collections.emptySet()
                    )
            );

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private DocumentSignatureTemplateService signatureTemplateService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UploadClientDocumentService uploadClientDocumentService;

    @Autowired
    private DocumentSignatureRequestDao signatureRequestDao;

    @Autowired
    private DocumentSignatureProvider documentSignatureProvider;

    @Autowired
    private DocumentFileService documentFileService;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private SignatureHistoryFileStorage signatureHistoryFileStorage;

    @Autowired
    private SignatureNotificationService signatureNotificationService;

    @Autowired
    private SignatureCancelNotificationService signatureCancelNotificationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private DocumentSignatureTemplateFieldService signatureTemplateFieldService;

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    private DocumentSignatureRequestSpecificationGenerator documentSignatureRequestSpecificationGenerator;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DocumentSignaturePdfService signaturePdfService;

    @Override
    @Transactional(readOnly = true)
    public DocumentSignatureRequest findById(Long id) {
        return signatureRequestDao.findById(id).orElseThrow();
    }

    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return signatureRequestDao.findById(id, projection).orElseThrow();
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return signatureRequestDao.findByIdIn(ids, projection);
    }

    @Override
    public Optional<DocumentSignatureRequest> findByPdcflowSignatureId(BigInteger pdcFlowSignatureId) {
        return signatureRequestDao.findByPdcflowSignatureId(pdcFlowSignatureId);
    }

    @Override
    @Transactional
    public List<DocumentSignatureRequest> submitRequests(List<SubmitTemplateSignatureRequest> dtos) {
        var map = dtos.stream()
                .collect(Collectors.groupingBy(submitRequest -> submitRequest.getTemplateContext().getClient().getId()));

        return map.values().stream()
                .map(requests -> {
                    var client = requests.get(0).getTemplateContext().getClient();

                    var generatedPin = generatePin(Objects.requireNonNull(client));

                    var signatureRequests = requests.stream()
                            .map(this::createNewSignatureRequest)
                            .map(request -> {
                                request.setPdcflowPinCode(generatedPin);
                                return request;
                            })
                            .map(this::submitRequest)
                            .collect(Collectors.toList());

                    signatureRequests.stream()
                            .findFirst()
                            .ifPresent(request -> {
                                if (request.getStatus().isSignatureRequestSentStatus()) {
                                    signatureNotificationService.sendRequestSignaturePinCodeSmsNotification(request);
                                }
                            });
                    return signatureRequests;
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private DocumentSignatureRequest submitRequest(DocumentSignatureRequest request) {
        request = sendSignatureRequestToProvider(request);
        if (request.getStatus().isSignatureRequestSentStatus()) {
            signatureNotificationService.sendRequestSignatureNotification(request);
        }
        return request;
    }

    private DocumentSignatureRequest sendSignatureRequestToProvider(DocumentSignatureRequest request) {
        try {
            documentSignatureProvider.sendSignatureRequest(
                    request,
                    documentService.readDocumentAsBytes(request.getDocument())
            );

            if (hasSignatureAreas(request)) {
                request.setStatus(DocumentSignatureRequestStatus.SIGNATURE_REQUESTED);
            } else {
                request.setStatus(DocumentSignatureRequestStatus.REVIEW_REQUESTED);
            }

        } catch (Exception e) {
            logger.warn("Failed to submit signature request to provider:", e);
            request.setStatus(DocumentSignatureRequestStatus.REQUEST_FAILED);
            request.setPdcflowErrorDatetime(Instant.now());
            request.setPdcflowErrorMessage("Failed to submit signature request to provider");
        }
        request = signatureRequestDao.save(request);
        logger.info("Created signature request with status [{}]", request.getStatus());
        return request;
    }

    private boolean hasSignatureAreas(DocumentSignatureRequest request) {
        return request.getSubmittedFields().stream()
                .map(DocumentSignatureRequestSubmittedField::getPdcflowOverlayType)
                .filter(Objects::nonNull)
                .anyMatch(PdcFlowOverlayBoxType.signatureBoxIds()::contains);
    }

    private DocumentSignatureRequest createNewSignatureRequest(SubmitTemplateSignatureRequest dto) {
        var request = new DocumentSignatureRequest();

        request.setSignatureTemplate(dto.getTemplateContext().getTemplate());
        request.setRequestedBy(dto.getRequestedBy());
        request.setRequestedFromEmployee(dto.getEmployeeRecipient());
        request.setRequestedFromClient(dto.getClientRecipient());
        request.setNotificationMethod(dto.getNotificationMethod());
        request.setEmail(dto.getEmail());
        request.setPhoneNumber(dto.getPhone());
        request.setMessage(dto.getMessage());
        request.setDateCreated(Instant.now());
        request.setDateExpires(dto.getExpirationDate());
        request.setClient(dto.getTemplateContext().getClient());
        request.setStatus(DocumentSignatureRequestStatus.CREATED);
        request.setNotSubmittedFields(new ArrayList<>());
        request.setSubmittedFields(new ArrayList<>());
        if (dto.getBulkRequest() != null) {
            request.setBulkRequestId(dto.getBulkRequest().getId());
            request.setBulkRequest(dto.getBulkRequest());
        }

        populateSubmittedFields(dto.getTemplateContext(), request);
        populateDocument(request, dto.getTemplateContext().getDocument());

        return signatureRequestDao.save(request);
    }

    private void populateDocument(DocumentSignatureRequest request, Document document) {
        if (document == null) {
            document = createDocumentForClient(
                    request.getSignatureTemplate(),
                    request.getSubmittedFields(),
                    request.getClient(),
                    request.getRequestedBy()
            );
        } else {
            if (document.getSignatureRequestId() != null) {
                var historySignatureRequestIds = document.getHistorySignatureRequestIds() == null
                        ? new HashSet<Long>()
                        : document.getHistorySignatureRequestIds();
                historySignatureRequestIds.add(document.getSignatureRequestId());
                document.setHistorySignatureRequestIds(historySignatureRequestIds);
            } else {
                throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
            }
        }
        document.setSignatureRequest(request);
        request.setDocument(document);
    }

    private void populateSubmittedFields(DocumentSignatureTemplateContext context, DocumentSignatureRequest request) {
        if (context.getDocument() == null) {
            request.getSubmittedFields().addAll(signatureTemplateFieldService.createScSubmittedFields(context));
            signatureTemplateFieldService.addPdcFlowFieldsToRequest(context, request);
        } else {
            int pageOffset = getPageOffset(context);
            signatureTemplateFieldService.addPdcFlowFieldsToRequest(context, request);
            request.getSubmittedFields().forEach(field -> field.setPageNo((short) (field.getPageNo() + pageOffset)));
        }
        request.getSubmittedFields().forEach(field -> field.setSignatureRequest(request));
    }

    private int getPageOffset(DocumentSignatureTemplateContext context) {
        var originalTemplatePageCount = signatureTemplateService.getTemplatePdfPageSizes(context.getTemplate()).size();
        var documentPageCount = signaturePdfService.getPdfPageSizes(
                documentService.readDocumentAsBytes(context.getDocument())
        ).size();
        return documentPageCount - originalTemplatePageCount;
    }

    private Document createDocumentForClient(
            DocumentSignatureTemplate template,
            List<DocumentSignatureRequestSubmittedField> fields,
            Client client,
            Employee author
    ) {
        var documentName = template.getTitle() + ".pdf";
        var documentUploadData = new ClientDocumentUploadData(
                documentName,
                documentName,
                MediaType.APPLICATION_PDF_VALUE,
                new ByteArrayInputStream(signatureTemplateService.getTemplatePdf(template, fields)),
                client,
                author,
                SharingOption.ALL
        );

        return uploadClientDocumentService.upload(documentUploadData);
    }

    @Override
    @Transactional
    public void cancelRequest(Long id, Employee currentUser) {
        var request = findById(id);
        cancelRequest(request, currentUser);
    }

    private void cancelRequest(DocumentSignatureRequest request, Employee currentUser) {
        validateStateTransition(request, DocumentSignatureRequestStatus.CANCELED);

        request.setStatus(DocumentSignatureRequestStatus.CANCELED);
        request.setDateCanceled(Instant.now());
        request.setCanceledByEmployee(currentUser);

        signatureRequestDao.save(request);
        if (request.getDocument().getTemporaryDeletionTime() == null) {
            documentService.temporaryDelete(request.getDocument(), currentUser);
        }

        try {
            documentSignatureProvider.cancelRequest(request);
        } catch (SignatureAlreadyCanceledProviderException e) {
            logger.warn("Signature request id = {} already canceled", request.getId());
        }
    }

    @Override
    @Transactional
    public DocumentSignatureRequest renewRequest(
            Long requestId,
            Instant newExpirationDate,
            Employee currentEmployee
    ) {
        var request = findById(requestId);

        return renewRequest(request, newExpirationDate, currentEmployee);
    }

    @Override
    @Transactional
    public DocumentSignatureRequest renewRequest(DocumentSignatureRequest request, Instant newExpirationDate, Employee currentEmployee) {
        var status = DocumentSignatureRequestUtils.resolveCorrectRequestStatus(request);
        if (!canRenewByStatus(status)) {
            throw new ValidationException("Cannot renew request with status: " + status);
        }

        var newRequest = createSignatureRequestCopy(request);

        newRequest.setStatus(DocumentSignatureRequestStatus.CREATED);
        newRequest.setRequestedBy(currentEmployee);
        newRequest.setDateExpires(newExpirationDate);
        newRequest.setDateCreated(Instant.now());
        newRequest.setPdcflowPinCode(generatePin(request.getClient()));

        populateDocument(newRequest, request.getDocument());

        signatureRequestDao.save(newRequest);

        return submitRequest(newRequest);
    }

    @Override
    public boolean canRenewByStatus(DocumentSignatureRequestStatus status) {
        return status == DocumentSignatureRequestStatus.EXPIRED;
    }

    private DocumentSignatureRequest createSignatureRequestCopy(DocumentSignatureRequest source) {
        var newRequest = new DocumentSignatureRequest();
        newRequest.setClient(source.getClient());
        newRequest.setSignatureTemplate(source.getSignatureTemplate());
        newRequest.setEmail(source.getEmail());
        newRequest.setPhoneNumber(source.getPhoneNumber());
        newRequest.setNotificationMethod(source.getNotificationMethod());
        newRequest.setRequestedFromClient(source.getRequestedFromClient());
        newRequest.setRequestedFromEmployee(source.getRequestedFromEmployee());
        newRequest.setBulkRequest(source.getBulkRequest());
        newRequest.setBulkRequestId(source.getBulkRequestId());
        var newFields = source.getSubmittedFields().stream()
                .map(existing -> {
                    var newField = new DocumentSignatureRequestSubmittedField();
                    newField.setSignatureRequest(newRequest);
                    newField.setFieldType(existing.getFieldType());
                    newField.setPageNo(existing.getPageNo());
                    newField.setTopLeftX(existing.getTopLeftX());
                    newField.setTopLeftY(existing.getTopLeftY());
                    newField.setBottomRightY(existing.getBottomRightY());
                    newField.setBottomRightX(existing.getBottomRightX());
                    newField.setPdcflowOverlayType(existing.getPdcflowOverlayType());
                    newField.setValue(existing.getValue());
                    return newField;
                })
                .collect(Collectors.toList());
        newRequest.setSubmittedFields(newFields);

        return newRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareTeamRole> getAllowedRecipientRoles() {
        return RECIPIENT_ALLOWED_ROLES.stream()
                .map(careTeamRoleService::get)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesAware> getAllowedRecipientEmployees(Long clientId, Long documentId) {

        var byClientView = employeeSpecificationGenerator.byClientViewAccess(clientService.findById(clientId));
        var byRoleAllowed = employeeSpecificationGenerator.bySystemRoleIn(RECIPIENT_ALLOWED_ROLES);
        var notOnHoldCtm = Specification.not(employeeSpecificationGenerator.isOnHoldCtmForClient(clientId));

        var spec = byClientView.and(byRoleAllowed.and(notOnHoldCtm));

        if (documentId != null) {
            var document = documentService.findById(documentId, Document.class);
            if (document.getSignatureRequestId() != null) {
                var signatureRequestIds = new ArrayList<Long>();
                signatureRequestIds.add(document.getSignatureRequestId());
                if (CollectionUtils.isNotEmpty(document.getHistorySignatureRequestIds())) {
                    signatureRequestIds.addAll(document.getHistorySignatureRequestIds());
                }
                spec = Specification.not(employeeSpecificationGenerator.isSignerOfAnySignatureRequest(signatureRequestIds))
                        .and(spec);
            }
        }

        return employeeService.findAll(spec, IdNamesAware.class);
    }

    @Override
    @Transactional
    public void processStatusUpdateCallback(DocumentSignatureRequest request, DocumentSignatureRequestPdcFlowCallbackLog logEntry) {
        if (StringUtils.isEmpty(logEntry.getPdcflowErrorCode())) {
            setSigned(request, logEntry);
        } else {
            setSignatureFailed(request, logEntry);
        }

        signatureRequestDao.save(request);
    }

    @Override
    @Transactional
    public DocumentSignatureRequestNotification resendPin(Long requestId) {
        var request = findById(requestId);
        return signatureNotificationService.resendPin(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByOrganizationIdAndStatuses(Long organizationId, List<DocumentSignatureRequestStatus> statuses) {
        var byClientOrganizationId =
                documentSignatureRequestSpecificationGenerator.byClientOrganizationId(organizationId);
        var withStatuses =
                documentSignatureRequestSpecificationGenerator.withStatuses(statuses);
        return signatureRequestDao.count(byClientOrganizationId.and(withStatuses));
    }

    @Override
    public Long countByCommunityIdAndStatuses(Long communityId, List<DocumentSignatureRequestStatus> statuses) {
        var byCommunityId =
                documentSignatureRequestSpecificationGenerator.byClientCommunityId(communityId);
        var withStatuses =
                documentSignatureRequestSpecificationGenerator.withStatuses(statuses);
        return signatureRequestDao.count(byCommunityId.and(withStatuses));
    }

    @Async
    @Override
    @Transactional
    public void cancelRequestedByOrganizationIdAsync(Long organizationId, Long employeeId) {
        var allRequestedIds = findAllRequestedIdsByOrganizationId(organizationId)
                .stream()
                .filter(Objects::nonNull)
                .map(IdAware::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(allRequestedIds)) {
            var employee = employeeService.findById(employeeId, Employee.class);
            allRequestedIds.forEach(requestId -> {
                try {
                    cancelRequest(requestId, employee);
                } catch (Exception e) {
                    logger.warn("Cannot cancel request id = " + requestId, e);
                }
            });
        }
    }

    @Async
    @Override
    @Transactional
    public void cancelRequestedByCommunityIdAsync(Long organizationId, Long employeeId) {
        var byClientCommunityId =
                documentSignatureRequestSpecificationGenerator.byClientCommunityId(organizationId);

        var withRequestedStatus =
                documentSignatureRequestSpecificationGenerator.withStatuses(DocumentSignatureRequestStatus.signatureRequestSentStatuses());

        var requestedIds = signatureRequestDao.findAll(byClientCommunityId.and(withRequestedStatus), IdAware.class);

        if (CollectionUtils.isNotEmpty(requestedIds)) {
            var employee = employeeService.findById(employeeId, Employee.class);
            requestedIds.stream()
                    .map(IdAware::getId)
                    .forEach(requestId -> {
                        try {
                            cancelRequest(requestId, employee);
                        } catch (Exception e) {
                            logger.warn("Cannot cancel request id = " + requestId, e);
                        }
                    });
        }
    }

    @Override
    @Transactional
    public List<IdAware> findAllRequestedIdsByOrganizationId(Long organizationId) {
        var byClientOrganizationId =
                documentSignatureRequestSpecificationGenerator.byClientOrganizationId(organizationId);

        var withRequestedStatus =
                documentSignatureRequestSpecificationGenerator.withStatuses(DocumentSignatureRequestStatus.signatureRequestSentStatuses());
        return signatureRequestDao.findAll(byClientOrganizationId.and(withRequestedStatus), IdAware.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSignatureRequest> findAllByBulkRequestIdAndStatusIn(Long bulkRequestId, Collection<DocumentSignatureRequestStatus> statuses) {
        return signatureRequestDao.findAllByBulkRequestIdAndStatusIn(bulkRequestId, statuses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSignatureRequest> findAllByBulkRequestIdAndSignatureTemplateIdAndStatusIn(Long bulkRequestId, Long templateId, Collection<DocumentSignatureRequestStatus> statuses) {
        return signatureRequestDao.findAllByBulkRequestIdAndSignatureTemplateIdAndStatusIn(bulkRequestId, templateId, statuses);
    }

    @Override
    @Transactional
    public DocumentSignatureRequest save(DocumentSignatureRequest request) {
        return signatureRequestDao.save(request);
    }

    private void setSigned(DocumentSignatureRequest request, DocumentSignatureRequestPdcFlowCallbackLog logEntry) {
        var newStatus = request.getStatus() == DocumentSignatureRequestStatus.REVIEW_REQUESTED
                ? DocumentSignatureRequestStatus.REVIEWED
                : DocumentSignatureRequestStatus.SIGNED;

        validateStateTransition(request, newStatus);
        var bytes = documentSignatureProvider.getSignedDocument(request);
        updateDocumentBytes(request.getId(), request.getDocument(), bytes);
        request.setStatus(newStatus);
        request.setDateSigned(logEntry.getPdcflowCompletionDate());
        request.setSignedEvent(createDocumentSignedEvent(request));
    }


    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllByBulkRequestId(Long bulkRequestId, Class<P> projectionClass) {
        var byBulkRequestId =
                documentSignatureRequestSpecificationGenerator.byBulkRequestId(bulkRequestId);

        // if document is null when request was renewed
        var withDocumentIdIsNotNull = documentSignatureRequestSpecificationGenerator.withDocumentIsNotNull();

        return signatureRequestDao.findAll(byBulkRequestId.and(withDocumentIdIsNotNull), projectionClass);
    }

    @Async
    @Override
    @Transactional
    public void cancelRequestsForOnHoldCtmByClientIdAsync(Long clientId, Long currentEmployeeId) {
        var byClientId = documentSignatureRequestSpecificationGenerator.byClientId(clientId);
        var withRequestedStatus =
                documentSignatureRequestSpecificationGenerator.withStatuses(DocumentSignatureRequestStatus.signatureRequestSentStatuses());
        var byRecipientCtmOnHold = documentSignatureRequestSpecificationGenerator.byRecipientCtmOnHold();

        cancelRequests(withRequestedStatus.and(byRecipientCtmOnHold.and(byClientId)), currentEmployeeId);
    }

    @Override
    @Transactional
    public void cancelRequestsForOnHoldCtmByCommunityIdAsync(Long communityId, Long currentEmployeeId) {

        var byClientCommunityId = documentSignatureRequestSpecificationGenerator.byClientCommunityId(communityId);
        var withRequestedStatus =
                documentSignatureRequestSpecificationGenerator.withStatuses(DocumentSignatureRequestStatus.signatureRequestSentStatuses());
        var byRecipientCtmOnHold = documentSignatureRequestSpecificationGenerator.byRecipientCtmOnHold();

        cancelRequests(withRequestedStatus.and(byRecipientCtmOnHold.and(byClientCommunityId)), currentEmployeeId);
    }

    private Event createDocumentSignedEvent(DocumentSignatureRequest request) {
        var eventAuthor = new EventAuthor();
        if (request.getRequestedFromEmployeeId() != null) {
            eventAuthor.setFirstName(request.getRequestedFromEmployee().getFirstName());
            eventAuthor.setLastName(request.getRequestedFromEmployee().getLastName());
            eventAuthor.setOrganization(request.getRequestedFromEmployee().getOrganization().getName());
            eventAuthor.setRole(request.getRequestedFromEmployee().getCareTeamRole().getName());
        } else {
            eventAuthor.setFirstName(request.getRequestedFromClient().getFirstName());
            eventAuthor.setLastName(request.getRequestedFromClient().getLastName());
            eventAuthor.setOrganization(request.getRequestedFromClient().getOrganization().getName());
            eventAuthor.setRole(careTeamRoleService.get(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES).getName());
        }

        var event = new Event();

        event.setEventAuthor(eventAuthor);
        event.setEventDateTime(request.getDateSigned());
        event.setEventType(eventTypeService.findByCode(EventNotificationUtils.DOCUMENT_SIGNED));

        clientService.findByOrganizationAlternativeIdAndLegacyId(
                        request.getDocument().getClientOrganizationAlternativeId(),
                        request.getDocument().getClientLegacyId()
                )
                .map(IdAware::getId)
                .map(clientService::findById)
                .ifPresent(event::setClient);

        return eventService.save(event);
    }

    private void cancelRequests(Specification<DocumentSignatureRequest> requestSpecification, Long currentEmployeeId) {
        var requests =
                signatureRequestDao.findAll(requestSpecification, DocumentSignatureRequest.class);

        if (CollectionUtils.isNotEmpty(requests)) {
            requests.forEach(request -> {
                try {
                    var employee = employeeService.findById(currentEmployeeId)
                            .orElse(null);
                    cancelRequest(request, employee);
                    signatureCancelNotificationService.sendCancelNotification(request);
                } catch (Exception e) {
                    logger.warn("Cannot cancel request id = " + request, e);
                }
            });
        }
    }

    private void updateDocumentBytes(Long signatureId, Document document, byte[] bytes) {
        try (var initialDoc = documentFileService.loadDocument(document)) {
            signatureHistoryFileStorage.save(initialDoc, signatureId.toString() + "-" + document.getId() + ".pdf");
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
        documentFileService.save(document, new ByteArrayInputStream(bytes));
        document.setHash(documentFileService.calculateDocumentHash(document));
        document.setSize((int) documentFileService.calculateDocumentSize(document));
        documentDao.save(document);
    }

    private void setSignatureFailed(DocumentSignatureRequest request, DocumentSignatureRequestPdcFlowCallbackLog logEntry) {
        validateStateTransition(request, DocumentSignatureRequestStatus.SIGNATURE_FAILED);
        request.setStatus(DocumentSignatureRequestStatus.SIGNATURE_FAILED);
        request.setPdcflowErrorCode(logEntry.getPdcflowErrorCode());
        request.setPdcflowErrorMessage(logEntry.getPdcflowErrorMessage());

        //todo is error == completion?
        request.setPdcflowErrorDatetime(logEntry.getPdcflowCompletionDate());
    }

    private void validateStateTransition(DocumentSignatureRequest request, DocumentSignatureRequestStatus newStatus) {
        var currentStatus = DocumentSignatureRequestUtils.resolveCorrectRequestStatus(request);
        if (!isValidStateTransition(currentStatus, newStatus)) {
            throw new RuntimeException("Invalid signature status transition: " + currentStatus.name() + " -> " + newStatus.name());
        }
    }

    private String generatePin(Client client) {
        if (client.getCommunity().getIsSignaturePinEnabled()) {
            return String.valueOf(random.get().nextInt(MAX_PIN - MIN_PIN) + MIN_PIN);
        } else {
            return null;
        }
    }

    private boolean isValidStateTransition(DocumentSignatureRequestStatus currentStatus, DocumentSignatureRequestStatus newStatus) {
        return VALID_STATUS_TRANSITIONS.get(currentStatus).contains(newStatus);
    }
}
