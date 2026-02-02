package com.scnsoft.eldermark.services.consol.templates.sections;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.VitalSign;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.VitalSignObservationFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.openhealthtools.mdht.uml.cda.consol.VitalSignsOrganizer;
import org.openhealthtools.mdht.uml.cda.consol.VitalSignsSectionEntriesOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Vital Signs</h1> “The section may contain all vital signs for the period
 * of time being summarized, but at a minimum should include notable vital signs
 * such as the most recent, maximum and/or minimum, or both, baseline, or
 * relevant trends.” [CCD 3.12]
 *
 * @see VitalSign
 * @see VitalSignObservation
 * @see Resident
 */
@Component("consol.VitalSignParser")
public class VitalSignParser
        extends AbstractParsableSection<VitalSignsOrganizer, VitalSignsSectionEntriesOptional, VitalSign>
        implements ParsableSection<VitalSignsSectionEntriesOptional, VitalSign> {

    private static final Logger logger = LoggerFactory.getLogger(VitalSignParser.class);
    private final String LEGACY_TABLE = "VitalSigns_NWHIN";

    private final VitalSignObservationFactory vitalSignObservationFactory;

    @Autowired
    public VitalSignParser(VitalSignObservationFactory vitalSignObservationFactory) {
        this.vitalSignObservationFactory = vitalSignObservationFactory;
    }

    @Override
    public List<VitalSign> doParseSection(Resident resident, VitalSignsSectionEntriesOptional vitalSignsSection) {
        if (!CcdParseUtils.hasContent(vitalSignsSection)
                || CollectionUtils.isEmpty(vitalSignsSection.getVitalSignsOrganizers())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<VitalSign> vitalSigns = new ArrayList<>();
        for (VitalSignsOrganizer vitalSignsOrganizer : vitalSignsSection.getVitalSignsOrganizers()) {
            final VitalSign vitalSign = new VitalSign();
            // TODO: inbound ID type is String
            vitalSign.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(vitalSignsOrganizer.getIds()));
            vitalSign.setResident(resident);
            vitalSign.setDatabase(resident.getDatabase());

            final IVL_TS effectiveTime = vitalSignsOrganizer.getEffectiveTime();
            vitalSign.setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate(effectiveTime));
            final List<VitalSignObservation> vitalSignObservations = parseVitalSignObservations(
                    vitalSignsOrganizer.getVitalSignObservations(), resident, vitalSign);
            vitalSign.setVitalSignObservations(filterVitalSignObservationList(vitalSignObservations));
            vitalSigns.add(vitalSign);
        }
        return vitalSigns;
    }

    private List<VitalSignObservation> filterVitalSignObservationList(
            List<VitalSignObservation> vitalSignObservations) {
        return FluentIterable.from(vitalSignObservations).filter(new Predicate<VitalSignObservation>() {
            @Override
            public boolean apply(VitalSignObservation vitalSignObservation) {
                return (vitalSignObservation != null && vitalSignObservation.getValue() != null
                        && StringUtils.isNotEmpty(vitalSignObservation.getValue().toString())
                        && StringUtils.isNotEmpty(vitalSignObservation.getUnit())
                        && vitalSignObservation.getResultTypeCode() != null
                        && StringUtils.isNotEmpty(vitalSignObservation.getResultTypeCode().getDisplayName()));
            }
        }).toList();
    }

    private List<VitalSignObservation> parseVitalSignObservations(
            EList<org.openhealthtools.mdht.uml.cda.consol.VitalSignObservation> vitalSignObservations,
            Resident resident, VitalSign vitalSign) {
        if (CollectionUtils.isEmpty(vitalSignObservations)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        // TODO test on real examples
        final List<VitalSignObservation> result = new ArrayList<>();
        for (org.openhealthtools.mdht.uml.cda.consol.VitalSignObservation ccdObservation : vitalSignObservations) {
            final VitalSignObservation vitalSignObservation = vitalSignObservationFactory.parse(ccdObservation,
                    resident, LEGACY_TABLE);
            vitalSignObservation.setVitalSign(vitalSign);
            result.add(vitalSignObservation);
        }
        return result;
    }

}
