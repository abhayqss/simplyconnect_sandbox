package com.scnsoft.eldermark.shared.utils;

import com.scnsoft.eldermark.shared.exception.PhrException;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.concurrent.Callable;

/**
 * Utility class for paged responses.
 *
 * @author phomal
 * Created on 9/27/2017.
 */
public final class PaginationUtils {

    private PaginationUtils() throws IllegalAccessException {
        throw new IllegalAccessException("PaginationUtils is non-instantiable.");
    }

    public static Pageable buildPageable(Integer maxResults, Integer page) {
        Pageable pageable = null;
        if (maxResults != null) {
            pageable = new PageRequest(page, maxResults);
        }
        return pageable;
    }

    public static Pageable buildPageable(Integer maxResults, Integer page, Sort sort) {
        Pageable pageable = null;
        if (maxResults != null) {
            pageable = new PageRequest(page, maxResults, sort);
        }
        return pageable;
    }

    /**
     * This method tries to infer a total number of objects based on the provided paging parameters.
     * And if it fails to find the total count using simple computations, then and only then the callback method is called.
     * It's designed for performance optimization and its' primary goal is to avoid an expensive SQL query where possible.
     *
     * @param callback a method that returns the exact total number of objects.
     */
    public static Long lazyTotalCount(int currentPageSize, Integer pageNumber, Integer maxPageSize, Callable<Long> callback) {
        if (maxPageSize == null || (pageNumber == 0 && currentPageSize == 0)) {
            return (long) currentPageSize;
        } else {
            if (currentPageSize > 0 && currentPageSize < maxPageSize) {
                return (long) (pageNumber * maxPageSize + currentPageSize);
            } else {
                try {
                    return callback.call();
                } catch (PhrException e) {
                    throw e;
                } catch (Exception e) {
                    throw new PhrException(e.getMessage());
                }
            }
        }
    }

    public static Pageable setSort(Pageable pageable, Sort.Order... sortOrders) {
        final Sort sort = new Sort(sortOrders);
        if (pageable == null) {
            return new PageRequest(0, Integer.MAX_VALUE, sort);
        }
        return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static <T> Page<T> buildEmptyPage() {
        return new PageImpl<>(Collections.<T>emptyList());
    }

}
