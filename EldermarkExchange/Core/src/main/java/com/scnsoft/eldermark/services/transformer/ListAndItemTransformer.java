package com.scnsoft.eldermark.services.transformer;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ListAndItemTransformer<S,T> implements Converter<S,T> {
    public void convertList(List<S> sourceList, List<T> targetList) {
        if (CollectionUtils.isNotEmpty(sourceList) && targetList != null) {
            for (S src : sourceList) {
                putIfPresent(targetList, convert(src));
            }
        }
    }

    public List<T> convertList(List<S> sourceList) {
        if (CollectionUtils.isNotEmpty(sourceList)) {
            final List<T> result = new ArrayList<>();
            convertList(sourceList, result);
            return result;
        }
        return Collections.emptyList();
    }

    private void putIfPresent(List<T> list, T t) {
        if (t != null) {
            list.add(t);
        }
    }
}
