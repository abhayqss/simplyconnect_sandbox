package com.scnsoft.eldermark.service.transformer.util;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * util converter class
 */
public class Converters {

    private Converters() {
    }

    /**
     *
     * @param sourceList list of source objects to be converted
     * @param converter converter which is used
     * @param <S>
     * @param <T>
     * @return list of converted DTOs
     */
    public static<S, T> List<T> convertAll(List<? extends S> sourceList, Converter<S, T> converter) {
        if (sourceList == null) {
            return Collections.emptyList();
        }
        final List<T> result = new ArrayList<>();
        for (final S s : sourceList) {
            if (s != null) {
                result.add(converter.convert(s));
            }
        }
        return result;
    }
}
