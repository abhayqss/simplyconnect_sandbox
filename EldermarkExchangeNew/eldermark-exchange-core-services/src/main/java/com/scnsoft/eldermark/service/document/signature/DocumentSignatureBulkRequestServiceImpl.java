package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureBulkRequestDao;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureBulkRequest;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureRequest;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureBulkRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DocumentSignatureBulkRequestServiceImpl implements DocumentSignatureBulkRequestService {

    @Autowired
    private DocumentSignatureBulkRequestDao bulkRequestDao;

    @Autowired
    private Converter<SubmitTemplateSignatureBulkRequest, List<SubmitTemplateSignatureRequest>> submitTemplateSignatureBulkRequestDtoConverter;

    @Autowired
    private DocumentSignatureRequestService signatureRequestService;

    @Override
    @Transactional
    public DocumentSignatureBulkRequest submitRequest(SubmitTemplateSignatureBulkRequest submitBulkRequest) {
        var savedBulkRequest = bulkRequestDao.save(new DocumentSignatureBulkRequest());
        var requests = Objects.requireNonNull(submitTemplateSignatureBulkRequestDtoConverter.convert(submitBulkRequest)).stream()
                .map(request -> {
                    request.setBulkRequest(savedBulkRequest);
                    return request;
                })
                .collect(Collectors.toList());

        signatureRequestService.submitRequests(requests);
        return savedBulkRequest;
    }

    @Override
    @Transactional
    public DocumentSignatureBulkRequest renewBulkRequest(
            Long bulkRequestId, Instant newExpirationDate, Long templateId, Employee currentEmployee
    ) {
        var requests =
                signatureRequestService.findAllByBulkRequestIdAndSignatureTemplateIdAndStatusIn(bulkRequestId, templateId, List.of(DocumentSignatureRequestStatus.EXPIRED));

        var newBulkRequest = bulkRequestDao.save(new DocumentSignatureBulkRequest());
        requests.forEach(request -> {
            request.setBulkRequest(newBulkRequest);
            request.setBulkRequestId(newBulkRequest.getId());
            signatureRequestService.renewRequest(request, newExpirationDate, currentEmployee);
        });

        return newBulkRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentSignatureBulkRequest findById(Long id) {
        return bulkRequestDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSignatureRequest> fetchRequestsByIdAndStatusIn(Long id, List<DocumentSignatureRequestStatus> statuses) {
        return signatureRequestService.findAllByBulkRequestIdAndStatusIn(id, statuses);
    }

    @Override
    @Transactional
    public void cancelBulkRequest(Long id, Long templateId, Employee currentEmployee) {
        var requests =
                signatureRequestService.findAllByBulkRequestIdAndSignatureTemplateIdAndStatusIn(id, templateId, DocumentSignatureRequestStatus.signatureRequestSentStatuses());
        requests.forEach(request -> signatureRequestService.cancelRequest(request.getId(), currentEmployee));
    }
}