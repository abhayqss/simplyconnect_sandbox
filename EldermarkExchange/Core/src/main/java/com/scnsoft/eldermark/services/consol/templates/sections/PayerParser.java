package com.scnsoft.eldermark.services.consol.templates.sections;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.Participant2Factory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.Performer2Factory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Procedure;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.datatypes.INT;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.eclipse.mdht.uml.hl7.vocab.x_DocumentActMood;
import org.openhealthtools.mdht.uml.cda.consol.CoverageActivity;
import org.openhealthtools.mdht.uml.cda.consol.PayersSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Payers</h1> “This section describes payers and the coverage they provide
 * for defined activities. For each payer, “all the pertinent data needed to
 * contact, bill to, and collect from that payer should be included.
 * Authorization information that can be used to define pertinent referral,
 * authorization tracking number, procedure, therapy, intervention, device, or
 * similar authorizations for the patient or provider or both should be
 * included.” [CCD 3.1]
 *
 * @see CoveragePlanDescription
 * @see Payer
 * @see Person
 * @see Participant
 * @see Organization
 * @see AuthorizationActivity
 * @see PolicyActivity
 * @see Resident
 * @see CcdCode
 */
@Component("consol.PayerParser")
public class PayerParser extends AbstractParsableSection<CoverageActivity, PayersSection, Payer>
        implements ParsableSection<PayersSection, Payer> {

    private static final Logger logger = LoggerFactory.getLogger(PayerParser.class);
    private static final String LEGACY_TABLE = "NWHIN_PAYER";
    private static final String PAYER_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.87";
    private static final String GUARANTOR_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.88";

    private final CcdCodeFactory ccdCodeFactory;
    private final Performer2Factory performer2Factory;
    private final Participant2Factory participant2Factory;

    @Autowired
    public PayerParser(CcdCodeFactory ccdCodeFactory, Performer2Factory performer2Factory,
            Participant2Factory participant2Factory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.performer2Factory = performer2Factory;
        this.participant2Factory = participant2Factory;
    }

    @Override
    public List<Payer> doParseSection(Resident resident, PayersSection payersSection) {
        if (!CcdParseUtils.hasContent(payersSection)
                || CollectionUtils.isEmpty(payersSection.getCoverageActivities())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<Payer> payers = new ArrayList<>();
        for (CoverageActivity ccdPayerCoverageActivity : payersSection.getCoverageActivities()) {
            final Payer payer = new Payer();
            payer.setDatabase(resident.getDatabase());
            payer.setResident(resident);
            payer.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdPayerCoverageActivity.getIds()));

            final II coverageActivityId = CcdParseUtils.getFirstNotEmptyValue(ccdPayerCoverageActivity.getIds(),
                    II.class);
            final Pair<String, String> rootAndExtension = CcdParseUtils.getRootAndExt(coverageActivityId);
            if (rootAndExtension != null)
                payer.setCoverageActivityId(rootAndExtension.getSecond());

            final List<PolicyActivity> policyActivities = parsePolicyActivities(resident, ccdPayerCoverageActivity);
            if (!CollectionUtils.isEmpty(policyActivities)) {
                for (PolicyActivity policyActivity : policyActivities) {
                    policyActivity.setPayer(payer);
                }
                payer.setPolicyActivities(filterPolicyActivityList(policyActivities));
            }
            payers.add(payer);
        }
        return payers;
    }

    private List<PolicyActivity> filterPolicyActivityList(List<PolicyActivity> policyActivities) {
        return FluentIterable.from(policyActivities).filter(new Predicate<PolicyActivity>() {
            @Override
            public boolean apply(PolicyActivity policyActivity) {
                return (policyActivity != null && policyActivity.getPayerOrganization() != null
                        && StringUtils.isNotEmpty(policyActivity.getPayerOrganization().getName())
                        && StringUtils.isNotEmpty(policyActivity.getParticipantMemberId()));
            }
        }).toList();
    }

    private List<PolicyActivity> parsePolicyActivities(Resident resident, CoverageActivity ccdPayerCoverageActivity) {
        if (CollectionUtils.isEmpty(ccdPayerCoverageActivity.getPolicyActivities()))
            return Collections.emptyList();

        final List<PolicyActivity> policyActivities = new ArrayList<>();
        for (org.openhealthtools.mdht.uml.cda.consol.PolicyActivity ccdPolicyActivity : ccdPayerCoverageActivity
                .getPolicyActivities()) {
            if (!CcdParseUtils.hasContent(ccdPolicyActivity))
                continue;

            final PolicyActivity policyActivity = new PolicyActivity();
            policyActivity.setDatabase(resident.getDatabase());

            policyActivity.setHealthInsuranceTypeCode(ccdCodeFactory.convert(ccdPolicyActivity.getCode()));
            policyActivity.setSequenceNumber(parseSequenceNumber(ccdPolicyActivity));

            // In order to distinguish between different types of performers (Payer and
            // Guarantor) we use templateId and code here
            List<Performer2> payerPerformers = CcdParseUtils.findByTemplateId(ccdPolicyActivity.getPerformers(),
                    PAYER_TEMPLATE_ID);
            if (CollectionUtils.isEmpty(payerPerformers)) {
                payerPerformers = CcdParseUtils.findByCode(ccdPolicyActivity.getPerformers(), "PAYOR");
            }
            final List<Performer2Factory.PayerWrapper> payerWrappers = performer2Factory.parsePayers(payerPerformers,
                    resident, LEGACY_TABLE);
            if (!CollectionUtils.isEmpty(payerWrappers)) {
                final Performer2Factory.PayerWrapper payerWrapper = payerWrappers.get(0);
                policyActivity
                        .setPayerFinanciallyResponsiblePartyCode(payerWrapper.getFinanciallyResponsiblePartyCode());
                policyActivity.setPayerOrganization(payerWrapper.getOrganization());

                // TODO where to save PayerPerson?
                // TODO is it possible to have more than one Payer performer?
                if (payerWrappers.size() > 1) {
                    logger.warn(
                            "DATA LOSS in parsePolicyActivities(): Some performers of type PAYOR are parsed but not persisted.");
                }
            }

            // In order to distinguish between different types of performers (Payer and
            // Guarantor) we use templateId and code here
            List<Performer2> guarantorPerformers = CcdParseUtils.findByTemplateId(ccdPolicyActivity.getPerformers(),
                    GUARANTOR_TEMPLATE_ID);
            if (CollectionUtils.isEmpty(guarantorPerformers)) {
                guarantorPerformers = CcdParseUtils.findByCode(ccdPolicyActivity.getPerformers(), "GUAR");
            }
            final List<Performer2Factory.GuarantorWrapper> guarantorWrappers = performer2Factory
                    .parseGuarantors(guarantorPerformers, resident, LEGACY_TABLE);
            if (!CollectionUtils.isEmpty(guarantorWrappers)) {
                final Performer2Factory.GuarantorWrapper guarantorWrapper = guarantorWrappers.get(0);
                policyActivity.setGuarantorTime(guarantorWrapper.getTime());
                policyActivity.setGuarantorOrganization(guarantorWrapper.getOrganization());
                policyActivity.setGuarantorPerson(guarantorWrapper.getPerson());

                // TODO is it possible to have more than one Payer performer?
                if (guarantorWrappers.size() > 1) {
                    logger.warn(
                            "DATA LOSS in parsePolicyActivities(): Some performers of type GUAR are parsed but not persisted.");
                }
            }

            for (Participant2 participant2 : ccdPolicyActivity.getParticipants()) {
                final Participant participant;
                switch (participant2.getTypeCode()) {
                case COV:
                    // Coverage target
                    participant = participant2Factory.parseCoverageTarget(participant2, policyActivity,
                            resident.getDatabase(), LEGACY_TABLE);
                    // TODO is it possible to have more than one Coverage Target participant?
                    if (policyActivity.getParticipant() != null) {
                        logger.warn(
                                "DATA LOSS in parsePolicyActivities(): Some participants of type COV are parsed but not persisted.");
                    }
                    policyActivity.setParticipant(participant);
                    break;
                case HLD:
                    // Holder
                    participant = participant2Factory.parseHolder(participant2, policyActivity, resident.getDatabase(),
                            LEGACY_TABLE);
                    // TODO is it possible to have more than one Holder participant?
                    if (policyActivity.getSubscriber() != null) {
                        logger.warn(
                                "DATA LOSS in parsePolicyActivities(): Some participants of type HLD are parsed but not persisted.");
                    }
                    policyActivity.setSubscriber(participant);
                    break;
                }
            }

            final List<AuthorizationActivity> authorizationActivities = new ArrayList<>();
            final List<CoveragePlanDescription> coveragePlanDescriptions = new ArrayList<>();
            for (Act act : ccdPolicyActivity.getActs()) {
                if (!CcdParseUtils.hasContent(act))
                    continue;

                if (act.getMoodCode() == x_DocumentActMood.DEF) {
                    // Description of Coverage Plan
                    final CoveragePlanDescription coveragePlanDescription = parseCoveragePlanDescription(resident, act);
                    coveragePlanDescription.setPolicyActivity(policyActivity);
                    coveragePlanDescriptions.add(coveragePlanDescription);
                } else if (act.getMoodCode() == x_DocumentActMood.EVN) {
                    // Authorization Activity
                    final AuthorizationActivity authorizationActivity = parseAuthorizationActivity(resident, act);
                    authorizationActivity.setPolicyActivity(policyActivity);
                    authorizationActivities.add(authorizationActivity);
                }
            }
            policyActivity.setAuthorizationActivities(authorizationActivities);
            policyActivity.setCoveragePlanDescriptions(coveragePlanDescriptions);

            policyActivities.add(policyActivity);
        }

        return policyActivities;
    }

    private AuthorizationActivity parseAuthorizationActivity(Resident resident, Act act) {
        checkNotNull(act);

        final AuthorizationActivity authorizationActivity = new AuthorizationActivity();
        authorizationActivity.setDatabase(resident.getDatabase());

        final List<CcdCode> clinicalStatements = new ArrayList<>();
        for (Procedure procedure : act.getProcedures()) {
            if (CcdParseUtils.hasContent(procedure)) {
                clinicalStatements.add(ccdCodeFactory.convert(procedure.getCode()));
            }
        }
        clinicalStatements.remove(null);
        authorizationActivity.setClinicalStatements(clinicalStatements);

        final List<Person> persons = new ArrayList<>();
        for (Performer2 performer2 : act.getPerformers()) {
            Person performer = performer2Factory.parsePerson(performer2, resident.getDatabase(), LEGACY_TABLE);
            if (performer != null) {
                persons.add(performer);
            }
        }
        authorizationActivity.setPerformers(persons);

        return authorizationActivity;
    }

    private CoveragePlanDescription parseCoveragePlanDescription(Resident resident, Act act) {
        checkNotNull(act);

        final CoveragePlanDescription coveragePlanDescription = new CoveragePlanDescription();
        coveragePlanDescription.setDatabase(resident.getDatabase());
        coveragePlanDescription.setLegacyId(CcdParseUtils.getFirstIdExtension(act.getIds()));

        coveragePlanDescription.setText(
                CcdTransform.EDtoString(act.getText(), act.getCode() == null ? null : act.getCode().getDisplayName()));

        return coveragePlanDescription;
    }

    private static BigInteger parseSequenceNumber(Act ccdPolicyActivity) {
        checkNotNull(ccdPolicyActivity);

        for (EntryRelationship entryRelationship : ccdPolicyActivity.getEntryRelationships()) {
            if (entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.COMP) {
                final INT sequenceNumber = entryRelationship.getSequenceNumber();
                if (CcdParseUtils.hasContent(sequenceNumber)) {
                    return sequenceNumber.getValue();
                }
            }
        }
        return null;
    }

}
