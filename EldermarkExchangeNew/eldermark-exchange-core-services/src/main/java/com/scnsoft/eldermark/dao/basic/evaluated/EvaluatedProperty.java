package com.scnsoft.eldermark.dao.basic.evaluated;

import com.scnsoft.eldermark.dao.basic.evaluated.processor.EvaluatedPropertyProcessor;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EvaluatedProperty {
    Class<? extends EvaluatedPropertyProcessor> value();
}
