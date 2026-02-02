package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import org.eclipse.mdht.uml.cda.AssignedEntity;
import org.eclipse.mdht.uml.cda.Performer2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 4/25/2018.
 */
@Component
public class Performer2Factory {

    private final CcdCodeFactory ccdCodeFactory;
    private final PersonFactory personFactory;

    @Autowired
    public Performer2Factory(CcdCodeFactory ccdCodeFactory, PersonFactory personFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.personFactory = personFactory;
    }

    public static class GuarantorWrapper {
        private Date time;
        private Organization organization;
        private Person person;

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public Organization getOrganization() {
            return organization;
        }

        public void setOrganization(Organization organization) {
            this.organization = organization;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }
    }

    public static class PayerWrapper {
        private CcdCode financiallyResponsiblePartyCode;
        private Organization organization;
        private Person person;

        public CcdCode getFinanciallyResponsiblePartyCode() {
            return financiallyResponsiblePartyCode;
        }

        public void setFinanciallyResponsiblePartyCode(CcdCode financiallyResponsiblePartyCode) {
            this.financiallyResponsiblePartyCode = financiallyResponsiblePartyCode;
        }

        public Organization getOrganization() {
            return organization;
        }

        public void setOrganization(Organization organization) {
            this.organization = organization;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }
    }

    public List<PayerWrapper> parsePayers(List<Performer2> performers, Resident resident, String LEGACY_TABLE) {
        if (CollectionUtils.isEmpty(performers)) {
            return Collections.emptyList();
        }

        final List<PayerWrapper> result = new ArrayList<>();
        for (Performer2 payerPerformer : performers) {
            if (!CcdParseUtils.hasContent(payerPerformer) || !CcdParseUtils.hasContent(payerPerformer.getAssignedEntity())) continue;

            final PayerWrapper payerWrapper = new PayerWrapper();
            final AssignedEntity assignedEntity = payerPerformer.getAssignedEntity();
            payerWrapper.setFinanciallyResponsiblePartyCode(ccdCodeFactory.convert(assignedEntity.getCode()));
            if (CollectionUtils.isEmpty(assignedEntity.getRepresentedOrganizations())) {
                final Person payerPerson = personFactory.parse(assignedEntity, resident.getDatabase(), LEGACY_TABLE);
                payerWrapper.setPerson(payerPerson);
            } else {
                // TODO added default organization
                payerWrapper.setOrganization(resident.getFacility());
            }

            result.add(payerWrapper);
        }

        return result;
    }

    public List<GuarantorWrapper> parseGuarantors(List<Performer2> performers, Resident resident, String LEGACY_TABLE) {
        if (CollectionUtils.isEmpty(performers)) {
            return Collections.emptyList();
        }

        final List<GuarantorWrapper> result = new ArrayList<>();
        for (Performer2 guarantorPerformer : performers) {
            if (!CcdParseUtils.hasContent(guarantorPerformer) || !CcdParseUtils.hasContent(guarantorPerformer.getAssignedEntity())) continue;

            final GuarantorWrapper guarantorWrapper = new GuarantorWrapper();
            final AssignedEntity assignedEntity = guarantorPerformer.getAssignedEntity();
            guarantorWrapper.setTime(CcdTransform.IVLTStoCenterDateOrTsToDate(guarantorPerformer.getTime()));
            if (CollectionUtils.isEmpty(assignedEntity.getRepresentedOrganizations())) {
                final Person guarantorPerson = personFactory.parse(assignedEntity, resident.getDatabase(), LEGACY_TABLE);
                guarantorWrapper.setPerson(guarantorPerson);
            } else {
                // TODO added default organization
                guarantorWrapper.setOrganization(resident.getFacility());
            }

            result.add(guarantorWrapper);
        }

        return result;
    }

    public Person parsePerson(Performer2 ccdPerformer2, Database database, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdPerformer2)) {
            return null;
        }

        return personFactory.parse(ccdPerformer2.getAssignedEntity(), database, legacyTable);
    }

}
