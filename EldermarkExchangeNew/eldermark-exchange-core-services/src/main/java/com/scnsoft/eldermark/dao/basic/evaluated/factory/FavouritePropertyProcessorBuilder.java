package com.scnsoft.eldermark.dao.basic.evaluated.factory;

import com.scnsoft.eldermark.dao.basic.evaluated.EvaluatedProperty;
import com.scnsoft.eldermark.dao.basic.evaluated.processor.FavouritePropertyProcessor;
import org.springframework.stereotype.Component;

@Component
public class FavouritePropertyProcessorBuilder implements EvaluatedPropertyProcessorBuilder<FavouritePropertyProcessor> {

    @Override
    public FavouritePropertyProcessor createEvaluatedPropertyProcessor(EvaluatedProperty evaluatedProperty) {
        return new FavouritePropertyProcessor();
    }

    @Override
    public Class<FavouritePropertyProcessor> getSupportedType() {
        return FavouritePropertyProcessor.class;
    }
}
