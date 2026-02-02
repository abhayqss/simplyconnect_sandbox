package com.scnsoft.eldermark.dao.basic.evaluated.factory;

import com.scnsoft.eldermark.dao.basic.evaluated.EvaluatedProperty;
import com.scnsoft.eldermark.dao.basic.evaluated.processor.EvaluatedPropertyProcessor;

public interface EvaluatedPropertyProcessorFactory {

    EvaluatedPropertyProcessor createEvaluatedPropertyProcessor(EvaluatedProperty evaluatedProperty);

}
