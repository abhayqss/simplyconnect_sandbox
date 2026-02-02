package com.scnsoft.eldermark.service.document.templates.cda.factory.header;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SingleHeaderFactory;
import org.eclipse.mdht.uml.cda.PatientRole;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClinicalDocument (C-CDA) SHALL contain at least one [1..*] recordTarget (CONF:5266, CONF:1198-5266)
 */
public interface RecordTargetFactory extends SingleHeaderFactory<PatientRole, Client> {
    @Transactional(propagation = Propagation.MANDATORY)
    Client parseSection(Community community, PatientRole header);
}
