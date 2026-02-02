package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.SingleHeaderFactory;
import org.eclipse.mdht.uml.cda.PatientRole;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClinicalDocument (C-CDA) SHALL contain at least one [1..*] recordTarget (CONF:5266, CONF:1198-5266)
 */
public interface RecordTargetFactory extends SingleHeaderFactory<PatientRole, Resident> {
    @Transactional(propagation = Propagation.MANDATORY)
    Resident parseSection(Organization organization, PatientRole header);
}
