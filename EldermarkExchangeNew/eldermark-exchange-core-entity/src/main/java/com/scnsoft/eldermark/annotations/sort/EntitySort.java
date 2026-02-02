package com.scnsoft.eldermark.annotations.sort;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface EntitySort {

    String value() default "";

    String[] joined() default {};

    /**
     * Use if single dto class can be mapped to multiple entity classes.
     * Usage: wrap multiple EntitySort in EntitySort.List with different target
     * entity classes.
     */
    Class<?> entity() default DefaultEntity.class;


    @Documented
    @Retention(RUNTIME)
    @Target(FIELD)
    @interface List {
        EntitySort[] value();
    }
}
