package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.mockito.stubbing.Answer;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
public class MockitoAnswers {

    public static Answer<CcdCode> returnConvertedCode() {
        return invocation -> {
            final CD src = invocation.getArgument(0, CD.class);
            if ((!CcdParseUtils.hasContent(src)) || (src.getCode() == null) || (src.getCodeSystem() == null))
                return null;

            final CcdCode code = new CcdCode();
            code.setCode(src.getCode());
            code.setDisplayName(src.getDisplayName());
            code.setCodeSystem(src.getCodeSystem());
            code.setCodeSystemName(src.getCodeSystemName());
            return code;
        };
    }

}
