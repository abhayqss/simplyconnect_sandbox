package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;
import org.springframework.stereotype.Component;

@Component
public class UnderlineSubmittedScFieldBuilder extends ValueSubmittedScFieldBuilder {

    public UnderlineSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.UNDERLINE, SignatureSubmittedFieldType.UNDERLINE);
    }
}
