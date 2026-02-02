package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.SubstanceAdministrationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.apache.commons.lang.StringUtils;
import org.openhealthtools.mdht.uml.cda.consol.MedicationActivity;
import org.openhealthtools.mdht.uml.cda.consol.MedicationsSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <h1>Medications</h1> “The Medications section defines a patient’s current
 * medications and pertinent medication history.” [CCD 3.9]
 *
 * @see Medication
 * @see MedicationInformation
 * @see Instructions
 * @see Indication
 * @see Client
 */
@Component("consol.MedicationsParser")
public class MedicationsParser extends AbstractParsableSection<MedicationActivity, MedicationsSection, Medication>
        implements ParsableSection<MedicationsSection, Medication> {

    private final SubstanceAdministrationFactory substanceAdministrationFactory;

    @Autowired
    public MedicationsParser(SubstanceAdministrationFactory substanceAdministrationFactory) {
        this.substanceAdministrationFactory = substanceAdministrationFactory;
    }

    @Override
    public boolean isSectionIgnored(MedicationsSection medicationsSection) {
        return !CcdParseUtils.hasContent(medicationsSection)
                || CollectionUtils.isEmpty(medicationsSection.getMedications());
    }

    @Override
    public List<Medication> doParseSection(Client resident, MedicationsSection medicationsSection) {
        Objects.requireNonNull(resident);

        final List<Medication> medications = medicationsSection.getMedications().stream()
                .map(medicationActivity -> substanceAdministrationFactory.parseMedicationActivity(medicationActivity,
                        resident, "Medication_NWHIN"))
                .filter(this::filterMedications)
                .collect(Collectors.toList());

        return medications;
    }

    private boolean filterMedications(Medication medication) {
        return (medication != null && medication.getMedicationInformation() != null
                && StringUtils.isNotEmpty(medication.getMedicationInformation().getProductNameText()));
    }
}
