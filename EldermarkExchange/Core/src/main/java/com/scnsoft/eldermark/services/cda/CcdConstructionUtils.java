package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.header.*;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author phomal
 * Created on 4/27/2018.
 */
@Component
public class CcdConstructionUtils {

    @Autowired
    private RecordTargetFactory recordTargetFactory;
    @Autowired
    private ParticipantFactory participantFactory;
    @Autowired
    private LegalAuthenticatorFactory legalAuthenticatorFactory;
    @Autowired
    private InFulfillmentOfFactory inFulfillmentOfFactory;
    @Autowired
    private InformationRecipientFactory informationRecipientFactory;
    @Autowired
    private InformantFactory informantFactory;
    @Autowired
    private DocumentationOfFactory documentationOfFactory;
    @Autowired
    private DataEntererFactory dataEntererFactory;
    @Autowired
    private CustodianFactory custodianFactory;
    @Autowired
    private ComponentFactory componentFactory;
    @Autowired
    private AuthorizationFactory authorizationFactory;
    @Autowired
    private AuthorFactory authorFactory;
    @Autowired
    private AuthenticatorFactory authenticatorFactory;

    public void constructHeaders(ClinicalDocument document, ClinicalDocumentVO srcDocument) {
        checkNotNull(document);
        checkNotNull(srcDocument);

        // setting document-level templateIds manually is not right... leave this job to MDHT library
        /*
        for (II templateId : buildTemplateIds()) {
            document.getTemplateIds().add(templateId);
        }*/
        //document.getRealmCodes().add(buildRealmCode());
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
            final Collection<Informant12> informant12s = informantFactory.buildTemplateInstance(srcDocument.getInformants());
            document.getInformants().addAll(informant12s);
        }
        if (custodianFactory.isTemplateIncluded()) {
            document.setCustodian(custodianFactory.buildTemplateInstance(srcDocument.getCustodian()));
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            final Collection<org.eclipse.mdht.uml.cda.InformationRecipient> informationRecipients = informationRecipientFactory.buildTemplateInstance(srcDocument.getInformationRecipients());
            document.getInformationRecipients().addAll(informationRecipients);
        }
        if (legalAuthenticatorFactory.isTemplateIncluded()) {
            document.setLegalAuthenticator(legalAuthenticatorFactory.buildTemplateInstance(srcDocument.getLegalAuthenticator()));
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            final Collection<org.eclipse.mdht.uml.cda.Authenticator> authenticators = authenticatorFactory.buildTemplateInstance(srcDocument.getAuthenticators());
            document.getAuthenticators().addAll(authenticators);
        }
        if (participantFactory.isTemplateIncluded()) {
            final Collection<Participant1> participant1s = participantFactory.buildTemplateInstance(srcDocument.getParticipants());
            document.getParticipants().addAll(participant1s);
        }
        if (inFulfillmentOfFactory.isTemplateIncluded()) {
            final Collection<InFulfillmentOf> inFulfillmentOfs = inFulfillmentOfFactory.buildTemplateInstance(srcDocument.getInFulfillmentOfs());
            document.getInFulfillmentOfs().addAll(inFulfillmentOfs);
        }
        if (authorizationFactory.isTemplateIncluded()) {
            final Collection<Authorization> authorizations = authorizationFactory.buildTemplateInstance(srcDocument.getAuthorizations());
            document.getAuthorizations().addAll(authorizations);
        }
        if (componentFactory.isTemplateIncluded()) {
            document.setComponent(componentFactory.buildTemplateInstance(srcDocument.getComponent()));
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            // if list of DocumentationOf is empty -> generate default for a Resident
            final Collection<com.scnsoft.eldermark.entity.DocumentationOf> srcDocumentationOfs;
            if (CollectionUtils.isEmpty(srcDocument.getDocumentationOfs())) {
                final Resident resident = srcDocument.getRecordTarget();
                srcDocumentationOfs = Collections.singleton(documentationOfFactory.generateDefault(resident));
            } else {
                srcDocumentationOfs = srcDocument.getDocumentationOfs();
            }
            final Collection<org.eclipse.mdht.uml.cda.DocumentationOf> documentationOfs = documentationOfFactory.buildTemplateInstance(srcDocumentationOfs);
            document.getDocumentationOfs().addAll(documentationOfs);
        }
    }

    private CS buildRealmCode() {
        CS realmCode = DatatypesFactory.eINSTANCE.createCS();
        realmCode.setCode("US");
        return realmCode;
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

    /**
     * Identifiers of the document templates and header templates used
     */
    private List<II> buildTemplateIds() {
        II templateId_1 = DatatypesFactory.eINSTANCE.createII();
        templateId_1.setRoot("2.16.840.1.113883.10.20.22.1.1");

        II templateId_2 = DatatypesFactory.eINSTANCE.createII();
        templateId_2.setRoot("2.16.840.1.113883.10.20.22.1.2");

        List<II> list = new ArrayList<>();
        list.add(templateId_1);
        list.add(templateId_2);
        return list;
    }

    /**
     * A unique identifier for the document
     */
    private II buildId() {
        String uuid = UUID.randomUUID().toString();
        return CcdUtils.getId(uuid);
    }

    /**
     * Descriptive title (may be self-evident from ClinicalDocument/code)
     */
    private ST buildTitle() {
        return DatatypesFactory.eINSTANCE.createST("Continuity Of Care Document");
    }

    private TS buildEffectiveTime() {
        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        effectiveTime.setValue(CcdUtils.formatDate(new Date()));
        return effectiveTime;
    }

    private CE buildConfidentialityCode() {
        CE confidentialityCode = DatatypesFactory.eINSTANCE.createCE();
        // N = Normal, R = Restricted, V = Very Restricted
        confidentialityCode.setCode("N");
        confidentialityCode.setCodeSystem("2.16.840.1.113883.5.25");
        confidentialityCode.setCodeSystemName("ConfidentialityCode");
        return confidentialityCode;
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

    private INT buildVersionNumber() {
        return null;
    }

}
