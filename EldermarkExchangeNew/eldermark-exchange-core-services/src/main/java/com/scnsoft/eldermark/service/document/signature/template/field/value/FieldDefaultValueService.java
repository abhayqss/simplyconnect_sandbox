package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FieldDefaultValueService {
    private final Map<TemplateFieldDefaultValueType, FieldDefaultValueBuilder> builders;

    @Autowired
    public FieldDefaultValueService(List<FieldDefaultValueBuilder> builders) {
        this.builders = builders.stream()
                .collect(Collectors.toUnmodifiableMap(
                        FieldDefaultValueBuilder::getTemplateFieldType,
                        Function.identity()
                ));
    }

    public Object findDefaultValue(TemplateFieldDefaultValueType type, DocumentSignatureTemplateContext context) {

        var builder = builders.get(type);

        if (builder == null) {
            throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
        }

        return builder.build(context);
    }
}
