package com.scnsoft.eldermark.dao.basic;

import com.scnsoft.eldermark.dao.basic.evaluated.processor.EvaluatedPropertyProcessor;
import org.springframework.data.mapping.PropertyPath;

public class InputProperty {
    private final EvaluatedPropertyProcessor evaluatedPropertyProcessor;
    private final PropertyPath propertyPath;
    private final String alias;

    public InputProperty(EvaluatedPropertyProcessor evaluatedPropertyProcessor, PropertyPath propertyPath, String alias) {
        this.evaluatedPropertyProcessor = evaluatedPropertyProcessor;
        this.propertyPath = propertyPath;
        this.alias = alias;
    }

    public EvaluatedPropertyProcessor getEvaluatedPropertyProcessor() {
        return evaluatedPropertyProcessor;
    }

    public PropertyPath getPropertyPath() {
        return propertyPath;
    }

    public String getAlias() {
        return alias;
    }
}
