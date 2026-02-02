package com.scnsoft.eldermark.service.document.cda.generator;

import com.scnsoft.eldermark.cda.service.schema.CdaDocumentType;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.exception.ApplicationException;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.cda.CdaGenerator;
import com.scnsoft.eldermark.service.document.cda.ClinicalDocumentService;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CcdGeneratorServiceImpl implements CcdGeneratorService {
    private static final int BUFFER_SIZE = 1024;
    private final Map<CdaDocumentType, CdaGenerator> generators;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClinicalDocumentService clinicalDocumentService;

    @Autowired
    public CcdGeneratorServiceImpl(List<CdaGenerator> cdaGenerators) {
        generators = cdaGenerators.stream().collect(Collectors.toMap(CdaGenerator::getGeneratedType, Function.identity()));
    }

    @Override
    public DocumentReport metadata() {
        DocumentReport document = new DocumentReport();
        document.setDocumentTitle("CCD.XML");
        document.setMimeType("text/xml");
        document.setDocumentType(DocumentType.CCD);
        return document;
    }

    private DocumentReport createReportFromStream(Client client, ByteArrayOutputStream buffer, String mimeType) {
        DocumentReport document = new DocumentReport();
        document.setDocumentTitle(String.format("ccd_%s_%s.xml", client.getFirstName(), client.getLastName()));
        document.setMimeType(mimeType);
        document.setDocumentType(DocumentType.CCD);
        document.setInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        return document;
    }

    @Override
    @Transactional
    public DocumentReport generate(Long clientId, boolean aggregated) {
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream(BUFFER_SIZE);
            exportXml(buffer, clientId, CdaDocumentType.CCDA_R1_1_CCD_V1, aggregated);

            return createReportFromStream(clientService.getById(clientId), buffer, MediaType.TEXT_XML_VALUE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void exportXml(OutputStream os, Long sourceClientId, CdaDocumentType docType, boolean aggregated) {
        var clients = aggregated ? clientService.findAllMergedClientsIds(sourceClientId) : Collections.singletonList(sourceClientId);

        exportXml(os, sourceClientId, docType, clients);
    }

    private void exportXml(OutputStream os, Long sourceClientId, CdaDocumentType type, List<Long> clientIds) {
        Objects.requireNonNull(os);
        Objects.requireNonNull(sourceClientId);
        Objects.requireNonNull(type);

        try {
            var generator = generators.get(type);
            if (generator == null) {
                throw new ApplicationException("Document type [" + type.name() + "] generation is not implemented");
            }

            var clinicalDocument = clinicalDocumentService.getClinicalDocument(sourceClientId, clientIds);
            var cda = generator.generate(clinicalDocument);

            CDAUtil.save(cda, os);
        } catch (Exception exc) {
            throw new BusinessException(exc.getMessage(), exc);
        }
    }
}