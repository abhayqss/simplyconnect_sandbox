package org.openhealthtools.openxds.registry.patient.helpers;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class Converters {
    Converters() {
    }

    public static <S,T> List<T> convertAll(List<S> srcList, Converter<S,T> converter) {
        final List<T> result = new ArrayList<T>(srcList.size());
        for (S s : srcList) {
            result.add(converter.convert(s));
        }
        return result;
    }
}
