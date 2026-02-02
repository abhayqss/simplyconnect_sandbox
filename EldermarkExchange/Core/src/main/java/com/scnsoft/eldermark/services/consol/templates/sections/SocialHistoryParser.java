package com.scnsoft.eldermark.services.consol.templates.sections;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;
import org.openhealthtools.mdht.uml.cda.consol.SocialHistorySection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Social History</h1> “This section contains data defining the patient’s
 * occupational, personal (e.g. lifestyle), social, and environmental history
 * and health risk factors, as well as administrative data such as marital
 * status, race, ethnicity and religious affiliation.” [CCD 3.7]
 *
 * The social history section shall contain a narrative description of the
 * person’s beliefs, home life, community life, work life, hobbies, and risky
 * habits.
 *
 * @see SocialHistory
 * @see SocialHistoryObservation
 * @see SmokingStatusObservation
 * @see TobaccoUse
 * @see PregnancyObservation
 * @see Resident
 */
@Component("consol.SocialHistoryParser")
public class SocialHistoryParser
        extends AbstractParsableSection<InfrastructureRoot, SocialHistorySection, SocialHistory>
        implements ParsableSection<SocialHistorySection, SocialHistory> {

    private static final Logger logger = LoggerFactory.getLogger(SocialHistoryParser.class);

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public SocialHistoryParser(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    private TobaccoUse parseTobaccoUseObservation(Observation ccdObservation, Resident resident) {
        if (!CcdParseUtils.hasContent(ccdObservation)) {
            return null;
        }
        checkNotNull(resident);

        final TobaccoUse tobaccoUse = new TobaccoUse();
        tobaccoUse.setDatabase(resident.getDatabase());

        final Pair<Date, Date> effectiveTime = CcdTransform
                .IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime());
        if (effectiveTime != null) {
            tobaccoUse.setEffectiveTimeLow(effectiveTime.getSecond());
        }
        final CD observationValue = ObservationFactory.getValue(ccdObservation, CD.class);
        tobaccoUse.setValue(ccdCodeFactory.convert(observationValue));

        return tobaccoUse;
    }

    private SmokingStatusObservation parseSmokingStatusObservation(Observation ccdObservation, Resident resident) {
        if (!CcdParseUtils.hasContent(ccdObservation)) {
            return null;
        }
        checkNotNull(resident);

        final SmokingStatusObservation smokingStatusObservation = new SmokingStatusObservation();
        smokingStatusObservation.setDatabase(resident.getDatabase());

        final Pair<Date, Date> effectiveTime = CcdTransform
                .IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime());
        if (effectiveTime != null) {
            smokingStatusObservation.setEffectiveTimeLow(effectiveTime.getSecond());
            smokingStatusObservation.setEffectiveTimeHigh(effectiveTime.getFirst());
        }
        final CD observationValue = ObservationFactory.getValue(ccdObservation, CD.class);
        smokingStatusObservation.setValue(ccdCodeFactory.convert(observationValue));

        return smokingStatusObservation;
    }

    private static PregnancyObservation parsePregnancyObservation(
            org.openhealthtools.mdht.uml.cda.consol.PregnancyObservation ccdObservation, Resident resident) {
        if (!CcdParseUtils.hasContent(ccdObservation)) {
            return null;
        }
        checkNotNull(resident);

        final PregnancyObservation pregnancyObservation = new PregnancyObservation();
        pregnancyObservation.setDatabase(resident.getDatabase());

        final Pair<Date, Date> effectiveTime = CcdTransform
                .IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime());
        if (effectiveTime != null) {
            pregnancyObservation.setEffectiveTimeLow(effectiveTime.getSecond());
        }
        // TODO test on real examples
        if (CcdParseUtils.hasContent(ccdObservation.getEstimatedDateOfDelivery())) {
            // This clinical statement represents the anticipated date when a woman will
            // give birth.
            for (Observation ccdEstimatedDateOfDeliveryObservation : ccdObservation.getEstimatedDateOfDelivery()
                    .getObservations()) {
                final TS observationTime = ObservationFactory.getValue(ccdEstimatedDateOfDeliveryObservation, TS.class);
                if (observationTime != null) {
                    pregnancyObservation.setEstimatedDateOfDelivery(CcdParseUtils.convertTsToDate(observationTime));
                    break;
                }
            }
        }

        return pregnancyObservation;
    }

    /**
     * The {@code <value>} element reports the value associated with the social
     * history observation. The data type to use for each observation should be
     * drawn from the table below.
     * <table border = "1">
     * <caption>Social History Codes</caption> <tbody>
     * <tr>
     * <th>Code</th>
     * <th>Description</th>
     * <th>Data Type</th>
     * <th>Units</th>
     * </tr>
     * <tr>
     * <td>229819007</td>
     * <td>Smoking</td>
     * <td rowspan="3" align="center">PQ</td>
     * <td>{pack}/d or {pack}/wk or {pack}/a</td>
     * </tr>
     * <tr>
     * <td>256235009</td>
     * <td>Exercise</td>
     * <td>{times}/wk</td>
     * </tr>
     * <tr>
     * <td>160573003</td>
     * <td>ETOH (Alcohol) Use</td>
     * <td>{drink}/d or {drink}/wk</td>
     * </tr>
     * <tr>
     * <td>364393001</td>
     * <td>Diet</td>
     * <td rowspan="4" align="center">CD</td>
     * <td rowspan="5"><center>N/A</center></td>
     * </tr>
     * <tr>
     * <td>364703007</td>
     * <td>Employment</td>
     * </tr>
     * <tr>
     * <td>425400000</td>
     * <td>Toxic Exposure</td>
     * </tr>
     * <tr>
     * <td>363908000</td>
     * <td>Drug Use</td>
     * </tr>
     * <tr>
     * <td>228272008</td>
     * <td>Other Social History</td>
     * <td align="center">ANY</td>
     * </tr>
     * </tbody>
     * </table>
     */
    private SocialHistoryObservation parseSocialHistoryObservation(Observation ccdObservation, Resident resident) {
        if (!CcdParseUtils.hasContent(ccdObservation)) {
            return null;
        }
        checkNotNull(resident);

        final SocialHistoryObservation socialHistoryObservation = new SocialHistoryObservation();
        socialHistoryObservation.setDatabase(resident.getDatabase());

        final CD code = ccdObservation.getCode();
        // (CONF:8555) Observation/value can be any data type.
        final ANY observationValueANY = ObservationFactory.getValue(ccdObservation, ANY.class);
        final ST observationValueST = observationValueANY instanceof ST ? (ST) observationValueANY : null;
        final CD observationValueCD = observationValueANY instanceof CD ? (CD) observationValueANY : null;
        if (CcdParseUtils.hasContent(observationValueCD)) {
            socialHistoryObservation.setValue(ccdCodeFactory.convert(observationValueCD));
        }
        if (code != null) {
            socialHistoryObservation.setType(ccdCodeFactory.convert(code));
            socialHistoryObservation
                    .setFreeText(CcdTransform.EDtoString(code.getOriginalText(), socialHistoryObservation.getValue()));
            if (CcdParseUtils.hasContent(observationValueST)) {
                socialHistoryObservation.setFreeTextValue(observationValueST.getText());
            }
        }
        // TODO add support for PQ
        final PQ observationValuePQ = observationValueANY instanceof PQ ? (PQ) observationValueANY : null;
        if (CcdParseUtils.hasContent(observationValuePQ)) {
            logger.warn("DATA LOSS: Observation value of type PQ is not persisted.");
        }

        return socialHistoryObservation;
    }

    private List<TobaccoUse> parseTobaccoUses(EList<org.openhealthtools.mdht.uml.cda.consol.TobaccoUse> tobaccoUses,
            Resident resident, SocialHistory socialHistory) {
        if (CollectionUtils.isEmpty(tobaccoUses)) {
            return Collections.emptyList();
        }
        final List<TobaccoUse> result = new ArrayList<>();
        for (org.openhealthtools.mdht.uml.cda.consol.TobaccoUse ccdTobaccoUse : tobaccoUses) {
            final TobaccoUse tobaccoUse = parseTobaccoUseObservation(ccdTobaccoUse, resident);
            if (tobaccoUse != null) {
                tobaccoUse.setSocialHistory(socialHistory);
                result.add(tobaccoUse);
            }
        }
        return result;
    }

    private List<SocialHistoryObservation> parseSocialHistoryObservations(
            EList<org.openhealthtools.mdht.uml.cda.consol.SocialHistoryObservation> socialHistoryObservations,
            Resident resident, SocialHistory socialHistory) {
        if (CollectionUtils.isEmpty(socialHistoryObservations)) {
            return Collections.emptyList();
        }
        final List<SocialHistoryObservation> result = new ArrayList<>();
        for (org.openhealthtools.mdht.uml.cda.consol.SocialHistoryObservation ccdSocialHistoryObservation : socialHistoryObservations) {
            final SocialHistoryObservation socialHistoryObservation = parseSocialHistoryObservation(
                    ccdSocialHistoryObservation, resident);
            if (socialHistoryObservation != null) {
                socialHistoryObservation.setSocialHistory(socialHistory);
                result.add(socialHistoryObservation);
            }
        }
        return result;
    }

    private List<SmokingStatusObservation> parseSmokingStatusObservations(
            EList<org.openhealthtools.mdht.uml.cda.consol.SmokingStatusObservation> smokingStatusObservations,
            Resident resident, SocialHistory socialHistory) {
        if (CollectionUtils.isEmpty(smokingStatusObservations)) {
            return Collections.emptyList();
        }
        final List<SmokingStatusObservation> result = new ArrayList<>();
        for (org.openhealthtools.mdht.uml.cda.consol.SmokingStatusObservation ccdSmokingStatusObservation : smokingStatusObservations) {
            final SmokingStatusObservation smokingStatusObservation = parseSmokingStatusObservation(
                    ccdSmokingStatusObservation, resident);
            if (smokingStatusObservation != null) {
                smokingStatusObservation.setSocialHistory(socialHistory);
                result.add(smokingStatusObservation);
            }
        }
        return result;
    }

    private static List<PregnancyObservation> parsePregnancyObservations(
            EList<org.openhealthtools.mdht.uml.cda.consol.PregnancyObservation> pregnancyObservations,
            Resident resident, SocialHistory socialHistory) {
        if (CollectionUtils.isEmpty(pregnancyObservations)) {
            return Collections.emptyList();
        }
        final List<PregnancyObservation> result = new ArrayList<>();
        for (org.openhealthtools.mdht.uml.cda.consol.PregnancyObservation ccdPregnancyObservation : pregnancyObservations) {
            final PregnancyObservation pregnancyObservation = parsePregnancyObservation(ccdPregnancyObservation,
                    resident);
            if (pregnancyObservation != null) {
                pregnancyObservation.setSocialHistory(socialHistory);
                result.add(pregnancyObservation);
            }
        }
        return result;
    }

    @Override
    public List<SocialHistory> doParseSection(Resident resident, SocialHistorySection socialHistorySection) {
        if (!CcdParseUtils.hasContent(socialHistorySection)
                || CollectionUtils.isEmpty(socialHistorySection.getObservations())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final SocialHistory socialHistory = new SocialHistory();
        socialHistory.setDatabase(resident.getDatabase());
        socialHistory.setResident(resident);
        II ii = socialHistorySection.getId();
        if (ii != null && ii.getExtension() != null) {
            socialHistory.setLegacyId(Long.parseLong(ii.getExtension()));
        } else {
            socialHistory.setLegacyId(0L);
        }

        final List<PregnancyObservation> pregnancyObservations = parsePregnancyObservations(
                socialHistorySection.getPregnancyObservations(), resident, socialHistory);
        final List<SmokingStatusObservation> smokingStatusObservations = parseSmokingStatusObservations(
                socialHistorySection.getSmokingStatusObservations(), resident, socialHistory);
        final List<SocialHistoryObservation> socialHistoryObservations = parseSocialHistoryObservations(
                socialHistorySection.getSocialHistoryObservations(), resident, socialHistory);
        final List<TobaccoUse> tobaccoUses = parseTobaccoUses(socialHistorySection.getTobaccoUses(), resident,
                socialHistory); // TODO : parse and store birth sex (BIRTH_SEX) ?

        socialHistory.setTobaccoUses(filterTobaccoUseList(tobaccoUses));
        socialHistory.setSmokingStatusObservations(filterSmokingStatusObservationList(smokingStatusObservations));
        socialHistory.setPregnancyObservations(filterPregnancyObservationList(pregnancyObservations));
        socialHistory.setSocialHistoryObservations(filterSocialHistoryObservations(socialHistoryObservations));

        return new ArrayList<>(Collections.singleton(socialHistory));
    }

    private List<SocialHistoryObservation> filterSocialHistoryObservations(
            List<SocialHistoryObservation> socialHistoryObservations) {
        return FluentIterable.from(socialHistoryObservations).filter(new Predicate<SocialHistoryObservation>() {
            @Override
            public boolean apply(SocialHistoryObservation socialHistoryObservation) {
                return (socialHistoryObservation != null
                        && ((socialHistoryObservation.getType() != null
                                && StringUtils.isNotEmpty(socialHistoryObservation.getType().getDisplayName()))
                                || (StringUtils.isNotEmpty(socialHistoryObservation.getFreeText())))
                        && ((socialHistoryObservation.getValue() != null
                                && StringUtils.isNotEmpty(socialHistoryObservation.getValue().getDisplayName()))
                                || (StringUtils.isNotEmpty(socialHistoryObservation.getFreeTextValue()))));
            }
        }).toList();
    }

    private List<SmokingStatusObservation> filterSmokingStatusObservationList(
            List<SmokingStatusObservation> smokingStatusObservations) {
        return FluentIterable.from(smokingStatusObservations).filter(new Predicate<SmokingStatusObservation>() {
            @Override
            public boolean apply(SmokingStatusObservation smokingStatusObservation) {
                return smokingStatusObservation != null && smokingStatusObservation.getValue() != null
                        && StringUtils.isNotEmpty(smokingStatusObservation.getValue().getDisplayName());
            }
        }).toList();
    }

    private List<PregnancyObservation> filterPregnancyObservationList(
            List<PregnancyObservation> pregnancyObservations) {
        return FluentIterable.from(pregnancyObservations).filter(new Predicate<PregnancyObservation>() {
            @Override
            public boolean apply(PregnancyObservation pregnancyObservation) {
                return (pregnancyObservation != null);
            }
        }).toList();
    }

    private List<TobaccoUse> filterTobaccoUseList(List<TobaccoUse> tobaccoUses) {
        return FluentIterable.from(tobaccoUses).filter(new Predicate<TobaccoUse>() {
            @Override
            public boolean apply(TobaccoUse tobaccoUse) {
                return (tobaccoUse != null && tobaccoUse.getValue() != null
                        && StringUtils.isNotEmpty(tobaccoUse.getValue().getDisplayName()));
            }
        }).toList();
    }

}
