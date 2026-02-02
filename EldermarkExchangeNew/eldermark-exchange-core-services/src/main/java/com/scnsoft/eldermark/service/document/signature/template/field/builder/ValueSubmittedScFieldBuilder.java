package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.*;
import com.scnsoft.eldermark.service.document.signature.template.field.value.FieldDefaultValueService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueSubmittedScFieldBuilder extends AbstractSubmittedScFieldBuilder {

    @Autowired
    private FieldDefaultValueService defaultValueService;

    private final SignatureSubmittedFieldType toType;
    private final Function<Object, String> valueMapper;

    public ValueSubmittedScFieldBuilder(
            ScSourceTemplateFieldType fromType,
            SignatureSubmittedFieldType toType
    ) {
        this(fromType, toType, ValueSubmittedScFieldBuilder::valueOf);
    }

    public ValueSubmittedScFieldBuilder(
            ScSourceTemplateFieldType fromType,
            SignatureSubmittedFieldType toType,
            Function<Object, String> valueMapper
    ) {
        super(fromType);
        this.toType = toType;
        this.valueMapper = valueMapper;
    }

    @Override
    public Stream<DocumentSignatureRequestSubmittedField> build(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateContext context
    ) {
        return extractValue(field, context)
                .stream()
                .map(fieldValue -> {
                    var submittedField = new DocumentSignatureRequestSubmittedField();
                    submittedField.setValue(fieldValue);
                    submittedField.setFieldType(toType);
                    populateFieldLocation(submittedField, field.getLocations().get(0));
                    return submittedField;
                });
    }

    private Optional<String> extractValue(DocumentSignatureTemplateField field, DocumentSignatureTemplateContext context) {
        if (
                context.getIsRequestFromMultipleClients()
                        && field.getDefaultValueType() != null
                        && field.getDefaultValueType().getIsClientField()
        ) {
            return getAutoFillFieldTitle(field)
                    .or(() -> getOrganizationAutoFillTypeTitle(field));
        } else {
            return findRawValueInTemplateContext(field, context)
                    .or(() -> findRawDefaultValue(field, context))
                    .map(valueMapper);
        }
    }

    private static Optional<String> getAutoFillFieldTitle(DocumentSignatureTemplateField field) {
        return Optional.ofNullable(field.getAutoFillType())
                .map(DocumentSignatureTemplateAutoFillFieldType::getTitle);
    }

    private static Optional<String> getOrganizationAutoFillTypeTitle(DocumentSignatureTemplateField field) {
        return Optional.ofNullable(field.getOrganizationAutoFillType())
                .map(DocumentSignatureTemplateOrganizationAutoFillFieldType::getTitle);
    }

    private Optional<Object> findRawValueInTemplateContext(
            DocumentSignatureTemplateField field,
            DocumentSignatureTemplateContext context
    ) {
        return Optional.ofNullable(context.getFieldValues().get(field.getName()));
    }

    private Optional<Object> findRawDefaultValue(DocumentSignatureTemplateField field, DocumentSignatureTemplateContext context) {
        if (field.getDefaultValueType() != null && context.getClient() != null) {
            return Optional.ofNullable(defaultValueService.findDefaultValue(field.getDefaultValueType(), context));
        } else {
            return Optional.empty();
        }
    }

    private static String valueOf(Object value) {
        if (value instanceof Collection) {
            var list = (Collection<?>) value;
            return list.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
        } else {
            return String.valueOf(value);
        }
    }
}
