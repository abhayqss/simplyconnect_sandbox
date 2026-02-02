package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractCommunitySubmittedScFieldBuilder extends AbstractSubmittedScFieldBuilder {

    private final SignatureSubmittedFieldType targetType;

    protected AbstractCommunitySubmittedScFieldBuilder(ScSourceTemplateFieldType supportedType) {
        this(supportedType, SignatureSubmittedFieldType.TEXT);
    }

    protected AbstractCommunitySubmittedScFieldBuilder(
            ScSourceTemplateFieldType supportedType,
            SignatureSubmittedFieldType targetType
    ) {
        super(supportedType);
        this.targetType = targetType;
    }

    abstract protected String extractValue(Community community);

    @Override
    public Stream<DocumentSignatureRequestSubmittedField> build(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateContext context
    ) {
        return Optional.ofNullable(context.getCommunity())
                .map(this::extractValue)
                .map(value -> {
                    var submittedField = new DocumentSignatureRequestSubmittedField();
                    submittedField.setValue(value);
                    submittedField.setFieldType(targetType);
                    populateFieldLocation(submittedField, field.getLocations().get(0));
                    return submittedField;
                })
                .stream();
    }
}
