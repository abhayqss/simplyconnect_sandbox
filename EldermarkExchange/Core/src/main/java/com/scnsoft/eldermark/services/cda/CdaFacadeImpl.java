package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.cda.service.DocumentTypeResolver;
import com.scnsoft.eldermark.cda.service.DocumentTypeResolverImpl;
import com.scnsoft.eldermark.cda.service.schema.DocumentType;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.ExchangeDocumentAwareBasicEntity;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.ccd.CcdHL7ConstructingService;
import com.scnsoft.eldermark.services.ccd.CcdHL7ParsingService;
import com.scnsoft.eldermark.services.consol.ConsolCcdConstructingService;
import com.scnsoft.eldermark.services.consol.ConsolCcdParsingService;
import com.scnsoft.eldermark.services.exceptions.cda.UnsupportedDocumentTypeException;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.exceptions.CcdGenerationException;
import com.scnsoft.eldermark.shared.exceptions.CdaViewGenerationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.services.cda.CdaFacade.ImportMode.CREATE;
import static com.scnsoft.eldermark.services.cda.CdaFacade.ImportMode.OVERWRITE;

/**
 * @author phomal
 * Created on 4/28/2018.
 */
@Component
public class CdaFacadeImpl implements CdaFacade {

    private final static Logger LOGGER = LoggerFactory.getLogger(CdaFacadeImpl.class);

    private final DocumentTypeResolver documentTypeResolver;
    private final ConsolCcdParsingService consolCcdParsingService;
    private final CcdHL7ParsingService ccdHL7ParsingService;
    private final ConsolCcdConstructingService consolCcdConstructingService;
    private final CcdHL7ConstructingService ccdHL7ConstructingService;
    private final MPIService mpiService;
    private final ResidentService residentService;
    private final ClinicalDocumentService clinicalDocumentService;

    @Autowired
    private Templates lantanaTransformationTemplate;

    @Autowired
    private DocumentFacade documentFacade;

    @Autowired
    public CdaFacadeImpl(ConsolCcdParsingService consolCcdParsingService,
                         CcdHL7ParsingService ccdHL7ParsingService,
                         ConsolCcdConstructingService consolCcdConstructingService,
                         CcdHL7ConstructingService ccdHL7ConstructingService,
                         MPIService mpiService,
                         ResidentService residentService,
                         ClinicalDocumentService clinicalDocumentService) {
        this.documentTypeResolver = new DocumentTypeResolverImpl();
        this.consolCcdParsingService = consolCcdParsingService;
        this.ccdHL7ParsingService = ccdHL7ParsingService;
        this.consolCcdConstructingService = consolCcdConstructingService;
        this.ccdHL7ConstructingService = ccdHL7ConstructingService;
        this.mpiService = mpiService;
        this.residentService = residentService;
        this.clinicalDocumentService = clinicalDocumentService;
    }

    /**
     * @param targetResident a resident record from database, if exists
     * @param importMode     Import mode defines what to do with
     */
    @Override
    public Resident importXml(InputStream is, Resident targetResident, Organization targetOrganization, ImportMode importMode) throws Exception {
        return importXml(is, targetResident, targetOrganization, importMode, null);
    }

    /**
     * @param targetResident a resident record from database, if exists
     * @param importMode     Import mode defines what to do with
     */
    @Override
    public Resident importXml(InputStream is, Resident targetResident, Organization targetOrganization, ImportMode importMode, Document exchangeDocument) throws Exception {
        // Step 1. load document
        final ClinicalDocument document = CDAUtil.load(is);
        try {
            final boolean documentValid = CDAUtil.validate(document);
            if (!documentValid) {
                LOGGER.error("CCD document is NOT valid!!! ");
            } else {
                LOGGER.info("CCD document is valid  residentId");
            }
        } catch (Exception e) {
            LOGGER.error("Exception during document validation: {}", ExceptionUtils.getStackTrace(e));
        }

        // Step 2. resolve document type
        final Set<DocumentType> types = documentTypeResolver.resolve(document);

        // Step 3. parse just a recordTarget header to get a resident for whom this document is generated
        final Resident ccdResident;
        if (types.contains(DocumentType.CCDA_R1_1_CCD_V1)) {
            ccdResident = consolCcdParsingService.parsePatientOnly(
                    (org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument) document, targetOrganization);
            //Resident patient = residentService.getResidentByIdentityFields(targetOrganization.getId(), resident.getSsn(),
            //resident.getDateOfBirth(), resident.getLastName(), resident.getFirstName());
        } else if (types.contains(DocumentType.HL7_CCD)) {
            ccdResident = ccdHL7ParsingService.parsePatientOnly(
                    (org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument) document, targetOrganization);
        } else {
            throw new UnsupportedDocumentTypeException(types.toString());
        }

        // Step 4. resolve target resident in database
        Resident dbResident;
        if (targetResident == null || CREATE.equals(importMode)) {
            // Step 4A. create a new resident record
            dbResident = residentService.createResident(ccdResident);
        } else {
            // Step 4B. update the existing resident record
            // if ImportMode is OVERWRITE -> delete all health data related to the resident before parsing
            merge(targetResident, ccdResident);
            dbResident = targetResident;
            if (OVERWRITE.equals(importMode)) {
                clinicalDocumentService.deleteByResidentId(dbResident.getId());
            }
            //dbResident = residentService.updateResident(targetResident);
        }

        // Step 5. parse document
        final ClinicalDocumentVO parsedDocument;
        if (types.contains(DocumentType.CCDA_R1_1_CCD_V1)) {
            parsedDocument = consolCcdParsingService.parse((ContinuityOfCareDocument) document, dbResident);
        } else if (types.contains(DocumentType.HL7_CCD)) {
            parsedDocument = ccdHL7ParsingService.parse((org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument) document, dbResident);
        } else {
            dbResident = null;
            parsedDocument = null;
        }


        if (parsedDocument != null) {
            // Step 6. populate exchange document
            if (exchangeDocument != null) {
                populateExchangeDocument(parsedDocument, exchangeDocument);
            }

            // Step 7. persist new health data
            clinicalDocumentService.saveClinicalDocument(dbResident, parsedDocument);
        }

        return dbResident;
    }

    @Override
    public Resident importXmlAsNewResident(InputStream is, Organization targetOrganization) throws Exception {
        return importXml(is, null, targetOrganization, CREATE);
    }

    private static void populateExchangeDocument(ClinicalDocumentVO parsedDocument, Document exchangeDocument) {
        parsedDocument.setPlanOfCares(populateExchangeDocument(parsedDocument.getPlanOfCares(), exchangeDocument));
    }

    private static <T extends ExchangeDocumentAwareBasicEntity> List<T> populateExchangeDocument(List<T> list, Document document) {
        for (T entry : list) {
            entry.setDocument(document);
        }
        return list;
    }

    /**
     * Copy sourceResident properties to targetResident properties so they are not different.
     *
     * @param targetResident a resident persisted in database
     * @param sourceResident a resident parsed from CCD
     */
    private void merge(Resident targetResident, Resident sourceResident) {
        if (sourceResident.getSocialSecurity() != null) {
            targetResident.setSocialSecurity(sourceResident.getSocialSecurity());
            targetResident.setSsnLastFourDigits(sourceResident.getSsnLastFourDigits());
        }
        // TODO more properties?
    }

    @Override
    public void exportXml(OutputStream os, Long sourceResidentId, DocumentType docType, boolean aggregated) {
        if (aggregated) {
            final List<Long> ids = mpiService.listResidentWithMergedResidents(sourceResidentId);
            exportXml(os, sourceResidentId, docType, ids);
        } else {
            exportXml(os, sourceResidentId, docType, Collections.singletonList(sourceResidentId));
        }
    }

    @Override
    public void exportXml(OutputStream os, Long sourceResidentId, DocumentType docType, List<Long> residentIds) {
        checkNotNull(os);
        checkNotNull(sourceResidentId);
        checkNotNull(docType);

        try {
            final ClinicalDocumentVO clinicalDocumentVO = clinicalDocumentService.getClinicalDocument(sourceResidentId, residentIds);

            final ClinicalDocument ccd;
            switch (docType) {
                case HL7_CCD:
                    ccd = ccdHL7ConstructingService.construct(clinicalDocumentVO);
                    break;
                case CCDA_R1_1_CCD_V1:
                    ccd = consolCcdConstructingService.construct(clinicalDocumentVO);
                    break;
                default:
                    throw new UnsupportedDocumentTypeException(String.valueOf(docType));
            }

            CDAUtil.save(ccd, os);
        } catch (Exception exc) {
            throw new CcdGenerationException(exc);
        }
    }

    @Override
    public void exportHtml(OutputStream os, Long sourceResidentId, DocumentType docType, boolean aggregated) {
        final ByteArrayOutputStream bTempOutput = new ByteArrayOutputStream(1024);
        exportXml(bTempOutput, sourceResidentId, docType, aggregated);

        final ByteArrayInputStream bInput = new ByteArrayInputStream(bTempOutput.toByteArray());
        try {
            transformCdaStreamWithXsl(bInput, os);
        } catch (TransformerException exc) {
            throw new CcdGenerationException(exc);
        }
    }

    @Override
    public String getCdaHtmlViewForDocument(Long documentId) {
        final DocumentBean document = documentFacade.findDocument(documentId);

        try {
            final ByteArrayInputStream bInput = new ByteArrayInputStream(Files.readAllBytes(document.getFile().toPath()));
            final ByteArrayOutputStream os = new ByteArrayOutputStream(1024);

            transformCdaStreamWithXsl(bInput, os);
            return os.toString();
        } catch (TransformerException | IOException e) {
            LOGGER.error("Error during CDA transformation for document id=[{}], reason {}", documentId, ExceptionUtils.getStackTrace(e));
            throw new CdaViewGenerationException(e);
        }
    }

    private void transformCdaStreamWithXsl(InputStream is, OutputStream os) throws TransformerException {
        final Transformer transformer = lantanaTransformationTemplate.newTransformer();
        transformer.transform(new StreamSource(is), new StreamResult(os));
    }
}
