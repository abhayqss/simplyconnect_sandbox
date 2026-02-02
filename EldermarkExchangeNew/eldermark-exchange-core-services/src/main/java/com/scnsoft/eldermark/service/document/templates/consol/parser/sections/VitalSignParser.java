package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.VitalSign;
import com.scnsoft.eldermark.entity.document.ccd.VitalSignObservation;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.VitalSignObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
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
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <h1>Vital Signs</h1> “The section may contain all vital signs for the period
 * of time being summarized, but at a minimum should include notable vital signs
 * such as the most recent, maximum and/or minimum, or both, baseline, or
 * relevant trends.” [CCD 3.12]
 *
 * @see VitalSign
 * @see VitalSignObservation
 * @see Client
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
    public boolean isSectionIgnored(VitalSignsSectionEntriesOptional vitalSignsSection) {
        return !CcdParseUtils.hasContent(vitalSignsSection)
                || CollectionUtils.isEmpty(vitalSignsSection.getVitalSignsOrganizers());
    }

    @Override
    public List<VitalSign> doParseSection(Client resident, VitalSignsSectionEntriesOptional vitalSignsSection) {
        Objects.requireNonNull(resident);

        final List<VitalSign> vitalSigns = new ArrayList<>();
        for (VitalSignsOrganizer vitalSignsOrganizer : vitalSignsSection.getVitalSignsOrganizers()) {
            final VitalSign vitalSign = new VitalSign();
            // TODO: inbound ID type is String
            vitalSign.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(vitalSignsOrganizer.getIds()));
            vitalSign.setClient(resident);
            vitalSign.setOrganization(resident.getOrganization());

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
        return vitalSignObservations.stream()
                .filter(vitalSignObservation -> (vitalSignObservation != null && vitalSignObservation.getValue() != null
                        && StringUtils.isNotEmpty(vitalSignObservation.getValue().toString())
                        && StringUtils.isNotEmpty(vitalSignObservation.getUnit())
                        && vitalSignObservation.getResultTypeCode() != null
                        && StringUtils.isNotEmpty(vitalSignObservation.getResultTypeCode().getDisplayName())))
                .collect(Collectors.toList());
    }

    private List<VitalSignObservation> parseVitalSignObservations(
            EList<org.openhealthtools.mdht.uml.cda.consol.VitalSignObservation> vitalSignObservations,
            Client resident, VitalSign vitalSign) {
        if (CollectionUtils.isEmpty(vitalSignObservations)) {
            return Collections.emptyList();
        }
        Objects.requireNonNull(resident);

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
