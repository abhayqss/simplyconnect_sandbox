package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;
import org.springframework.stereotype.Component;

@Component
public class TextSubmittedScFieldBuilder extends ValueSubmittedScFieldBuilder {

    public TextSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.TEXT, SignatureSubmittedFieldType.TEXT);
    }
}
