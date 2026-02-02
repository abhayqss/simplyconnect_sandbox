package com.scnsoft.eldermark.xds;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.facades.exceptions.EmployeeNotFoundException;
import com.scnsoft.eldermark.services.DocumentService;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.SaveDocumentCallback;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.services.cda.CdaFacade;
import com.scnsoft.eldermark.shared.exceptions.DocumentAlreadyStoredException;
import com.scnsoft.eldermark.shared.exceptions.ResidentNotFoundException;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by averazub on 9/27/2016.
 */
@Component
public class XdsFacadeImpl implements XdsFacade {

    @Autowired
    ResidentService residentService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    DocumentService documentService;

    @Autowired
    private CdaFacade cdaFacade;

    private static final Logger LOGGER = LoggerFactory.getLogger(XdsFacadeImpl.class);

    @Override
    public Document saveDocument(DocumentMetadata metadata, Long residentId, String uuid, String uniqueId, String hash, SaveDocumentCallback callback) {

        Document documentOld = documentService.findDocumentByUniqueId(uniqueId);
        if (documentOld != null) {
            if (documentOld.getHash().equals(hash)) {
                //The same document is saved previously. Allow it to proceed.
                return documentOld;
            }
            System.err.println("document unique id " + uniqueId + " already exist");
            throw new DocumentAlreadyStoredException("document unique id " + uniqueId + " already exist in repository");
        }


        Employee author = employeeService.getActiveEmployee("xdsuser@eldermark.com","RBA"); //TODO
        if (author == null) {
            throw new EmployeeNotFoundException(-1);
        }

        Resident resident = residentService.getResident(residentId);
        if (resident==null) throw new ResidentNotFoundException("Resident with Id "+residentId+" Not Found in the system");

        try {
            Document document = documentService.saveDocumentFromXds(metadata, resident, author, uuid, uniqueId, callback);
            return document;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long saveAndParseCDA(final XdsDocument xdsDocument, final DocumentMetadata documentMetadata,
                                final Long residentId, final  String uuid, final  String uniqueId, final  String hash,
                                final SaveDocumentCallback callback) {
        Validate.<Object>noNullElements(new Object[]{xdsDocument, documentMetadata, residentId, callback},
                "Method parameters cannot be null");
        Validate.notEmpty(uuid);
        Validate.notEmpty(uniqueId);

        final Document document = saveDocument(documentMetadata, residentId, uuid, uniqueId, hash, callback);

        LOGGER.info("Parsing CCD; residentId = [{}] ; document title = [{}] ; document id = [{}]", residentId.toString(), xdsDocument.getTitle(), document.getId());
        final Resident resident = residentService.getResident(residentId);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(xdsDocument.getContent());
        final Organization organization = resident.getFacility();

        try {
            getCdaFacade().importXml(inputStream, resident, organization, CdaFacade.ImportMode.APPEND, document);
        } catch (Exception e) {
            LOGGER.warn("CDA parsing failed; Saving only raw file without parsing; residentId = [{}] ; document title = [{}] ; document id = [{}]", residentId.toString(), xdsDocument.getTitle(), document.getId());
            LOGGER.warn(ExceptionUtils.getStackTrace(e));
        }
        return document.getId();
    }

    @Override
    public Document getDocument(String documentUniqueId) {
        Document doc = documentService.findDocumentByUniqueIdOrThrow(documentUniqueId);
        return doc;
    }


    @Override
    public void deleteDocument(String documentUniqueId) throws IOException {
        Document doc = documentService.findDocumentByUniqueIdOrThrow(documentUniqueId);
        documentService.deleteDocumentFromXds(doc.getId());
    }

    public CdaFacade getCdaFacade() {
        return cdaFacade;
    }

    public void setCdaFacade(CdaFacade cdaFacade) {
        this.cdaFacade = cdaFacade;
    }
}
