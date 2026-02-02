package com.scnsoft.eldermark.converter.base;

import org.springframework.core.convert.converter.Converter;

public interface ItemConverter<S,T> extends Converter<S,T>{

    void convert(S source,T target);
}
