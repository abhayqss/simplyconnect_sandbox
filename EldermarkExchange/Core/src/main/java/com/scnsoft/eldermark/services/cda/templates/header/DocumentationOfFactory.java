package com.scnsoft.eldermark.services.cda.templates.header;

import com.scnsoft.eldermark.entity.DocumentationOf;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.MultiHeaderFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableMultiHeader;

/**
 * {@code ClinicalDocument} (C-CDA) SHALL contain exactly one [1..1] documentationOf (CONF:8452) according to
 * General Header Constraints (templateId = 2.16.840.1.113883.10.20.22.1.1) (2013)
 * <br/>
 * {@code ClinicalDocument} (C-CDA) MAY contain zero or more [0..*] documentationOf (CONF:1198-14835) according to
 * US Realm Header (V3) Constraints (templateId = 2.16.840.1.113883.10.20.22.1.1 : 2015-08-01) (2015)
 */
public interface DocumentationOfFactory extends
        MultiHeaderFactory<org.eclipse.mdht.uml.cda.DocumentationOf, com.scnsoft.eldermark.entity.DocumentationOf>,
        ParsableMultiHeader<org.eclipse.mdht.uml.cda.DocumentationOf, com.scnsoft.eldermark.entity.DocumentationOf> {
    DocumentationOf generateDefault(Resident resident);
}
