package com.scnsoft.eldermark.annotations.sort;

import org.springframework.data.domain.Sort;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({FIELD})
public @interface DefaultSort {
    Sort.Direction direction() default Sort.Direction.ASC;

    int order() default 0;
}
