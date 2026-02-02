package com.scnsoft.eldermark.service.document.cda.generator;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.DocumentationOf;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.cda.factory.header.*;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class CcdConstructionUtils {

    @Autowired
    private AuthenticatorFactory authenticatorFactory;
    @Autowired
    private AuthorFactory authorFactory;
    @Autowired
    private AuthorizationFactory authorizationFactory;
    @Autowired
    private ComponentFactory componentFactory;
    @Autowired
    private CustodianFactory custodianFactory;
    @Autowired
    private DataEntererFactory dataEntererFactory;
    @Autowired
    private DocumentationOfFactory documentationOfFactory;
    @Autowired
    private InformantFactory informantFactory;
    @Autowired
    private InformationRecipientFactory informationRecipientFactory;
    @Autowired
    private InFulfillmentOfFactory inFulfillmentOfFactory;
    @Autowired
    private LegalAuthenticatorFactory legalAuthenticatorFactory;
    @Autowired
    private ParticipantFactory participantFactory;
    @Autowired
    private RecordTargetFactory recordTargetFactory;

    private CE buildConfidentialityCode() {
        CE confidentialityCode = DatatypesFactory.eINSTANCE.createCE();
        // N = Normal, R = Restricted, V = Very Restricted
        confidentialityCode.setCode("N");
        confidentialityCode.setCodeSystem("2.16.840.1.113883.5.25");
        confidentialityCode.setCodeSystemName("ConfidentialityCode");
        return confidentialityCode;
    }

    private TS buildEffectiveTime() {
        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        effectiveTime.setValue(CcdUtils.formatDate(new Date()));
        return effectiveTime;
    }

    /**
     * A unique identifier for the document
     */
    private II buildId() {
        String uuid = UUID.randomUUID().toString();
        return CcdUtils.getId(uuid);
    }

    /**
     * An IETF language tag
     */
    private CS buildLanguageCode() {
        return DatatypesFactory.eINSTANCE.createCS("en-US");
    }

    private II buildSetId() {
        return null;
    }

    /**
     * Descriptive title (may be self-evident from ClinicalDocument/code)
     */
    private ST buildTitle() {
        return DatatypesFactory.eINSTANCE.createST("Continuity Of Care Document");
    }

    /**
     * A “document type” identifier
     */
    private InfrastructureRootTypeId buildTypeId() {
        InfrastructureRootTypeId typeId = CDAFactory.eINSTANCE.createInfrastructureRootTypeId();
        typeId.setRoot("2.16.840.1.113883.1.3");
        typeId.setExtension("POCD_HD000040");
        return typeId;
    }

    private INT buildVersionNumber() {
        return null;
    }

    public void constructGeneralCCDAHeader(ClinicalDocument document, ClinicalDocumentVO srcDocument) {
        checkNotNull(document);
        checkNotNull(srcDocument);

        document.setTypeId(buildTypeId());
        document.setId(buildId());
        document.setTitle(buildTitle());
        document.setEffectiveTime(buildEffectiveTime());
        document.setConfidentialityCode(buildConfidentialityCode());
        document.setLanguageCode(buildLanguageCode());
        document.setSetId(buildSetId());
        document.setVersionNumber(buildVersionNumber());

        // header sections
        if (recordTargetFactory.isTemplateIncluded()) {
            document.addPatientRole(recordTargetFactory.buildTemplateInstance(srcDocument.getRecordTarget()));
        }
        if (authorFactory.isTemplateIncluded()) {
            final Collection<Author> authors = authorFactory.buildTemplateInstance(srcDocument.getAuthors());
            document.getAuthors().addAll(authors);
        }
        if (dataEntererFactory.isTemplateIncluded()) {
            document.setDataEnterer(dataEntererFactory.buildTemplateInstance(srcDocument.getDataEnterer()));
        }
        if (informantFactory.isTemplateIncluded()) {
            final Collection<Informant12> informant12s = informantFactory
                    .buildTemplateInstance(srcDocument.getInformants());
            document.getInformants().addAll(informant12s);
        }
        if (custodianFactory.isTemplateIncluded()) {
            document.setCustodian(custodianFactory.buildTemplateInstance(srcDocument.getCustodian()));
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            final Collection<org.eclipse.mdht.uml.cda.InformationRecipient> informationRecipients = informationRecipientFactory
                    .buildTemplateInstance(srcDocument.getInformationRecipients());
            document.getInformationRecipients().addAll(informationRecipients);
        }
        if (legalAuthenticatorFactory.isTemplateIncluded()) {
            document.setLegalAuthenticator(
                    legalAuthenticatorFactory.buildTemplateInstance(srcDocument.getLegalAuthenticator()));
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            final Collection<org.eclipse.mdht.uml.cda.Authenticator> authenticators = authenticatorFactory
                    .buildTemplateInstance(srcDocument.getAuthenticators());
            document.getAuthenticators().addAll(authenticators);
        }
        
       
        if (participantFactory.isTemplateIncluded()) {
            final Collection<Participant1> participant1s = participantFactory
                    .buildTemplateInstance(srcDocument.getParticipants());
            document.getParticipants().addAll(participant1s);
        }
     
        if (inFulfillmentOfFactory.isTemplateIncluded()) {
            final Collection<InFulfillmentOf> inFulfillmentOfs = inFulfillmentOfFactory
                    .buildTemplateInstance(srcDocument.getInFulfillmentOfs());
            document.getInFulfillmentOfs().addAll(inFulfillmentOfs);
        }
        if (authorizationFactory.isTemplateIncluded()) {
            final Collection<Authorization> authorizations = authorizationFactory
                    .buildTemplateInstance(srcDocument.getAuthorizations());
            document.getAuthorizations().addAll(authorizations);
        }
        if (componentFactory.isTemplateIncluded()) {
            document.setComponent(componentFactory.buildTemplateInstance(srcDocument.getComponent()));
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            // if list of DocumentationOf is empty -> generate default for a Resident
            final Collection<DocumentationOf> srcDocumentationOfs;
            if (CollectionUtils.isEmpty(srcDocument.getDocumentationOfs())) {
                final Client client = srcDocument.getRecordTarget();
                srcDocumentationOfs = Collections.singleton(documentationOfFactory.generateDefault(client));
            } else {
                srcDocumentationOfs = srcDocument.getDocumentationOfs();
            }
            final Collection<org.eclipse.mdht.uml.cda.DocumentationOf> documentationOfs = documentationOfFactory
                    .buildTemplateInstance(srcDocumentationOfs);
            document.getDocumentationOfs().addAll(documentationOfs);
            //todo [ccd] generate meaningful documentationOfs instead?
        }
    }

}