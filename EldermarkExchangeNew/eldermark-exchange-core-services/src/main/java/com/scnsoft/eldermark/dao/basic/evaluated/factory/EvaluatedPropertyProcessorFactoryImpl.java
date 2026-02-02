package com.scnsoft.eldermark.dao.basic.evaluated.factory;

import com.scnsoft.eldermark.dao.basic.evaluated.EvaluatedProperty;
import com.scnsoft.eldermark.dao.basic.evaluated.processor.EvaluatedPropertyProcessor;
import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EvaluatedPropertyProcessorFactoryImpl implements EvaluatedPropertyProcessorFactory {

    private final Map<Class<?>, EvaluatedPropertyProcessorBuilder<?>> buildersMap;

    public EvaluatedPropertyProcessorFactoryImpl(List<EvaluatedPropertyProcessorBuilder<?>> builders) {
        this.buildersMap = builders.stream()
                .collect(StreamUtils.toMapOfUniqueKeys(EvaluatedPropertyProcessorBuilder::getSupportedType));
    }

    @Override
    public EvaluatedPropertyProcessor createEvaluatedPropertyProcessor(EvaluatedProperty evaluatedProperty) {
        var processorType = evaluatedProperty.value();

        var builder = buildersMap.getOrDefault(processorType, null);

        if (builder == null) {
            throw new RuntimeException("Implement evaluated property processor creation, type " + processorType);
        }

        return builder.createEvaluatedPropertyProcessor(evaluatedProperty);
    }
}
