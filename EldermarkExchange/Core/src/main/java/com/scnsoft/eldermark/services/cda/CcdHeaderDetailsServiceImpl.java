package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.header.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author phomal
 * Created on 4/27/2018.
 */
@Service
public class CcdHeaderDetailsServiceImpl implements CcdHeaderDetailsService {

    @Autowired
    private RecordTargetFactory recordTargetFactory;
    @Autowired
    private AuthorFactory authorFactory;
    @Autowired
    private DataEntererFactory dataEntererFactory;
    @Autowired
    private InformantFactory informantFactory;
    @Autowired
    private CustodianFactory custodianFactory;
    @Autowired
    private InformationRecipientFactory informationRecipientFactory;
    @Autowired
    private LegalAuthenticatorFactory legalAuthenticatorFactory;
    @Autowired
    private AuthenticatorFactory authenticatorFactory;
    @Autowired
    private ParticipantFactory participantFactory;
    @Autowired
    private InFulfillmentOfFactory inFulfillmentOfFactory;
    @Autowired
    private DocumentationOfFactory documentationOfFactory;
    @Autowired
    private AuthorizationFactory authorizationFactory;
    @Autowired
    private ComponentFactory componentFactory;

    @Autowired
    private AuthorDao authorDao;
    @Autowired
    private DataEntererDao dataEntererDao;
    @Autowired
    private InformantDao informantDao;
    @Autowired
    private CustodianDao custodianDao;
    @Autowired
    private InformationRecipientDao informationRecipientDao;
    @Autowired
    private LegalAuthenticatorDao legalAuthenticatorDao;
    @Autowired
    private AuthenticatorDao authenticatorDao;
    @Autowired
    private ParticipantDao participantDao;
    @Autowired
    private DocumentationOfDao documentationOfDao;

    @Autowired
    private ResidentDao residentDao;

    @Override
    public CcdHeaderDetails getHeaderDetails(Long residentId) {
        checkNotNull(residentId);
        return getHeaderDetails(residentId, Collections.singletonList(residentId));
    }

    @Override
    public CcdHeaderDetails getHeaderDetails(Long mainResidentId, List<Long> residentIds) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return null;
        }
        final CcdHeaderDetails headerDto = new CcdHeaderDetails();

        // Restrictions of an aggregated clinical document header
        // * ClinicalDocument (C-CDA) SHOULD contain zero or one (0..1) legalAuthenticator (CONF:5579 [1], CONF:1198-5579 [2]).
        // * ClinicalDocument (C-CDA) MAY contain zero or one (0..1) dataEnterer (CONF:5441 [1], CONF:1198-28678 [2]).
        // * ClinicalDocument (C-CDA) SHALL contain exactly one (1..1) custodian (CONF:5519 [1], CONF:1198-5519 [2]).
        // * Though ClinicalDocument may contain multiple (1..*) recordTarget elements (Patient Details section in UI),
        //   I don't think it's the right way of representing a merged record.
        //
        // References:
        // [1] The first reference is to General Header Constraints (templateId = 2.16.840.1.113883.10.20.22.1.1), 2013
        // [2] The second reference is to US Realm Header (V3) Constraints (templateId = 2.16.840.1.113883.10.20.22.1.1 : 2015-08-01), 2015

        // header sections [0..1]
        if (dataEntererFactory.isTemplateIncluded()) {
            final DataEnterer dataEnterer = dataEntererDao.getCcdDataEnterer(mainResidentId);
            headerDto.setDataEnterer(dataEnterer);
        }
        if (custodianFactory.isTemplateIncluded()) {
            final Custodian custodian = custodianDao.getCcdCustodian(mainResidentId);
            headerDto.setCustodian(custodian);
        }
        if (legalAuthenticatorFactory.isTemplateIncluded()) {
            final LegalAuthenticator legalAuthenticator = legalAuthenticatorDao.getCcdLegalAuthenticator(mainResidentId);
            headerDto.setLegalAuthenticator(legalAuthenticator);
        }

        // header sections [0..*]
        if (authorFactory.isTemplateIncluded()) {
            headerDto.setAuthors(authorDao.listByResidentIds(residentIds));
        }
        if (informantFactory.isTemplateIncluded()) {
            headerDto.setInformants(informantDao.listByResidentIds(residentIds));
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            headerDto.setRecipients(informationRecipientDao.listByResidentIds(residentIds));
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            headerDto.setAuthenticators(authenticatorDao.listByResidentIds(residentIds));
        }
        if (participantFactory.isTemplateIncluded()) {
            headerDto.setParticipants(participantDao.listByResidentIds(residentIds));
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            headerDto.setDocumentationOfs(documentationOfDao.listByResidentIds(residentIds));
        }

        // stubs - not implemented for an unknown reason
        if (inFulfillmentOfFactory.isTemplateIncluded()) {
            headerDto.setInFulfillmentOfs(Collections.emptyList());
        }
        if (authorizationFactory.isTemplateIncluded()) {
            headerDto.setAuthorizations(Collections.emptyList());
        }
        if (componentFactory.isTemplateIncluded()) {
            headerDto.setComponent(Collections.emptyList());
        }

        return headerDto;
    }

    @Override
    public Resident getRecordTarget(Long residentId) {
        checkNotNull(residentId);
        if (recordTargetFactory.isTemplateIncluded()) {
            return residentDao.get(residentId);
        }
        return null;
    }

}
