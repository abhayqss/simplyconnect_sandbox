package com.scnsoft.eldermark.hl7v2.processor;

import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;

import java.util.Optional;

public interface CcdCodeResolverService {

    Optional<CcdCode> resolveCode(CECodedElement ce);

}
