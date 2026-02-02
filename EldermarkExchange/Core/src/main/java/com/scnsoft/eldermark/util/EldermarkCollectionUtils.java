package com.scnsoft.eldermark.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * collections utility class
 */
public class EldermarkCollectionUtils {
    private EldermarkCollectionUtils() {
    }

    /**
     *
     * @param element element to be transormed into list
     * @param <T>
     * @return empty list if the element is null, singltone list otherwise
     */
    public static<T> List<T> singltoneListOfNullableElement(T element) {
        if (element == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(element);
    }

    public static <E> List<E> listFromIterable(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        if(iterable == null || !iterable.iterator().hasNext()) return list;
        for (E item : iterable) {
            list.add(item);
        }
        return list;
    }

    public static<T> List<T> safeSubList(List<T> list, int indexFrom, int indexTo) {
        final int listSize = list.size();
        if (indexFrom < 0) {
            indexFrom = 0;
        }
        if (indexFrom > listSize) {
            indexFrom = listSize;
        }
        if (indexTo < 0) {
            indexTo = 0;
        }
        if (indexTo > listSize) {
            indexTo = listSize;
        }
        return list.subList(indexFrom, indexTo);
    }

}
