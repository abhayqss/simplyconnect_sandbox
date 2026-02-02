package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.SubstanceAdministrationFactory;

import org.apache.commons.lang.StringUtils;
import org.openhealthtools.mdht.uml.cda.consol.MedicationActivity;
import org.openhealthtools.mdht.uml.cda.consol.MedicationsSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Medications</h1> “The Medications section defines a patient’s current
 * medications and pertinent medication history.” [CCD 3.9]
 *
 * @see Medication
 * @see MedicationInformation
 * @see Instructions
 * @see Indication
 * @see Resident
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
    public List<Medication> doParseSection(Resident resident, MedicationsSection medicationsSection) {
        if (!CcdParseUtils.hasContent(medicationsSection)
                || CollectionUtils.isEmpty(medicationsSection.getMedications())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<Medication> medications = new ArrayList<>();
        for (MedicationActivity medicationActivity : medicationsSection.getMedications()) {
            final Medication medication = substanceAdministrationFactory.parseMedicationActivity(medicationActivity,
                    resident, "Medication_NWHIN");
            if (filterMedications(medication)) {
                medications.add(medication);
            }
        }
        return medications;
    }

    private boolean filterMedications(Medication medication) {
        return (medication != null && medication.getMedicationInformation() != null
                && StringUtils.isNotEmpty(medication.getMedicationInformation().getProductNameText()));
    }
}
