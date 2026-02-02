package com.scnsoft.eldermark.service.document.cda;

import com.scnsoft.eldermark.cda.service.schema.CdaDocumentType;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.exception.cda.UnsupportedCdaTypeException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.cda.parse.ccd.CcdHL7ParsingService;
import com.scnsoft.eldermark.service.document.cda.parse.consol.ConsolCcdParsingService;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Set;

@Service
@Transactional
public class CdaImportServiceImpl implements CdaImportService {

    private static final Logger logger = LoggerFactory.getLogger(CdaImportServiceImpl.class);

    @Autowired
    private ConsolCcdParsingService consolCcdParsingService;

    @Autowired
    private CcdHL7ParsingService ccdHL7ParsingService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClinicalDocumentService clinicalDocumentService;

    /**
     * @param targetResident a resident record from database, if exists
     * @param importMode     Import mode defines what to do with
     */
    @Override
    public Client importXml(InputStream is, Client targetResident, Community targetCommunity, ImportMode importMode) throws Exception {
        return importXml(is, targetResident, targetCommunity, importMode, null);
    }

    /**
     * @param targetResident a resident record from database, if exists
     * @param importMode     Import mode defines what to do with
     */
    @Override
    public Client importXml(InputStream is, Client targetResident, Community targetCommunity, ImportMode importMode, Document exchangeDocument) throws Exception {
        // Step 1. load document
        final ClinicalDocument document = CDAUtil.load(is);
        try {
            final boolean documentValid = CDAUtil.validate(document);
            if (!documentValid) {
                logger.error("CCD document is NOT valid!!!");
            } else {
                logger.info("CCD document is valid");
            }
        } catch (Exception e) {
            logger.error("Exception during document validation: {}", ExceptionUtils.getStackTrace(e));
        }

        // Step 2. resolve document type
        final Set<CdaDocumentType> types = CcdParseUtils.resolveCdaTypes(document);

        // Step 3. parse just a recordTarget header to get a resident for whom this document is generated
        final Client ccdResident;
        if (types.contains(CdaDocumentType.CCDA_R1_1_CCD_V1)) {
            ccdResident = consolCcdParsingService.parsePatientOnly(
                    (org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument) document, targetCommunity);
        } else if (types.contains(CdaDocumentType.HL7_CCD)) {
            ccdResident = ccdHL7ParsingService.parsePatientOnly(
                    (org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument) document, targetCommunity);
        } else {
            throw new UnsupportedCdaTypeException(types.toString());
        }

        // Step 4. resolve target resident in database
        Client dbResident;
        if (targetResident == null || ImportMode.CREATE.equals(importMode)) {
            // Step 4A. create a new resident record
            dbResident = clientService.save(ccdResident);
        } else {
            // Step 4B. update the existing resident record
            // if ImportMode is OVERWRITE -> delete all health data related to the resident before parsing
            copyProperties(ccdResident, targetResident);
            dbResident = targetResident;
            if (ImportMode.OVERWRITE.equals(importMode)) {
                clinicalDocumentService.deleteByResidentId(dbResident.getId());
            }
            //dbResident = residentService.updateResident(targetResident);
        }

        // Step 5. parse document
        final ClinicalDocumentVO parsedDocument;
        if (types.contains(CdaDocumentType.CCDA_R1_1_CCD_V1)) {
            parsedDocument = consolCcdParsingService.parse((ContinuityOfCareDocument) document, dbResident);
        } else if (types.contains(CdaDocumentType.HL7_CCD)) {
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

    /**
     * Copy sourceResident properties to targetResident properties so they are not different.
     *
     * @param target a resident persisted in database
     * @param source a resident parsed from CCD
     */
    private void copyProperties(Client source, Client target) {
        if (source.getSocialSecurity() != null) {
            target.setSocialSecurity(source.getSocialSecurity());
            target.setSsnLastFourDigits(source.getSsnLastFourDigits());
        }
        // TODO more properties?
    }

    private static void populateExchangeDocument(ClinicalDocumentVO parsedDocument, Document exchangeDocument) {
        parsedDocument.getPlanOfCares().forEach(e -> e.setDocument(exchangeDocument));
    }
}
