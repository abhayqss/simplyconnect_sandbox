package com.scnsoft.eldermark.api.shared.utils;

import com.scnsoft.eldermark.entity.basic.AuditableEntity_;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;
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

    public static Sort historySort() {
        return Sort.by(Sort.Order.desc(AuditableEntity_.LAST_MODIFIED_DATE),
                Sort.Order.desc(AuditableEntity_.ID));
    }


    public static Pageable buildPageable(Integer maxResults, Integer pageNumber) {
        return Optional.ofNullable(maxResults).
                map(max -> PageRequest.of(pageNumber, max))
                .orElse(null);
    }

    public static Pageable buildPageable(Integer maxResults, Integer pageNumber, Sort sort) {
        return Optional.ofNullable(maxResults).
                map(max -> PageRequest.of(pageNumber, max, sort))
                .orElse(null);
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

    public static Pageable setSort(Pageable pageable, Sort sort) {
        if (pageable == null || pageable.isUnpaged()) {
            return createLargestPage(sort);
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static Pageable createLargestPage() {
        return PageRequest.of(0, Integer.MAX_VALUE);
    }

    public static Pageable createLargestPage(Sort sort) {
        return PageRequest.of(0, Integer.MAX_VALUE, sort);
    }

    public static Pageable sortByDefault(Pageable pageable, Sort defaultSort) {
        if (!hasSort(pageable)) {
            return setSort(pageable, defaultSort);
        }
        return pageable;
    }

    public static boolean hasSort(Pageable pageable) {
        //todo find a better way to figure out if no sorting is applied to the pageable
        return pageable != null && pageable.getSort().isSorted();
    }


    public static <T> Page<T> buildEmptyPage() {
        return new PageImpl<>(Collections.<T>emptyList());
    }

}
