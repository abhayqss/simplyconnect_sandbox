package com.scnsoft.eldermark.annotations;

import java.lang.annotation.*;


@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SwaggerDoc {
}
