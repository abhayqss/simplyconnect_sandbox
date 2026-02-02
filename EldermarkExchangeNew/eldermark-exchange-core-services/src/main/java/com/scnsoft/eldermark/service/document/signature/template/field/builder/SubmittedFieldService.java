package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.BaseDocumentSignatureFieldStyle;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedFieldStyle;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateAutoFillFieldType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateOrganizationAutoFillFieldType;
import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SubmittedFieldService {

    private final Map<ScSourceTemplateFieldType, SubmittedScFieldBuilder> builders;

    @Autowired
    public SubmittedFieldService(List<SubmittedScFieldBuilder> builders) {
        this.builders = builders.stream()
                .collect(Collectors.toUnmodifiableMap(
                        SubmittedScFieldBuilder::getTemplateFieldType,
                        Function.identity()
                ));
    }

    public List<DocumentSignatureRequestSubmittedField> constructScFields(
            DocumentSignatureTemplateContext templateContext
    ) {
        return templateContext.getTemplate().getFields().stream()
                .filter(field -> field.getScSourceFieldType() != null)
                .filter(field -> field.getPdcFlowType() == null || templateContext.getFieldValues().get(field.getName()) != null)
                .flatMap(field -> {
                    var builder = builders.get(field.getScSourceFieldType());
                    if (builder == null) {
                        throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
                    }
                    return builder.build(field, templateContext)
                            .peek(submittedField -> addStylesToSubmittedField(submittedField, field.getStyles()));
                })
                .collect(Collectors.toList());
    }

    public void addStylesToSubmittedField(
            DocumentSignatureRequestSubmittedField field,
            List<? extends BaseDocumentSignatureFieldStyle> styles
    ) {
        if (field.getStyles() == null) field.setStyles(new ArrayList<>());
        styles.stream()
                .map(style -> {
                    var submittedStyle = new DocumentSignatureRequestSubmittedFieldStyle();
                    submittedStyle.setSubmittedField(field);
                    submittedStyle.setType(style.getType());
                    submittedStyle.setValue(style.getValue());
                    return submittedStyle;
                })
                .forEach(field.getStyles()::add);
    }
}
