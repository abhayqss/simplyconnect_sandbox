package com.scnsoft.eldermark.web.commons.utils;

import com.scnsoft.eldermark.annotations.sort.DefaultEntity;
import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.basic.AuditableEntity_;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for paged responses.
 */
public final class PaginationUtils {

    private PaginationUtils() throws IllegalAccessException {
        throw new IllegalAccessException("PaginationUtils is non-instantiable.");
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

    public static Pageable setSort(Pageable pageable, Sort.Order... sortOrders) {
        final Sort sort = Sort.by(sortOrders);
        return setSort(pageable, sort);
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

    public static Pageable defaultPage(Pageable pageable) {
        return pageable != null ? pageable : createLargestPage();
    }

    public static Sort historySort() {
        return Sort.by(Sort.Order.desc(AuditableEntity_.LAST_MODIFIED_DATE),
                Sort.Order.desc(AuditableEntity_.ID));
    }

    public static Pageable sortByDefault(Pageable pageable, Sort defaultSort) {
        if (!hasSort(pageable)) {
            return setSort(pageable, defaultSort);
        }
        return pageable;
    }

    public static boolean hasSort(Pageable pageable) {
        //todo find a better way to figure out if no sorting is applied to the pageable
        return pageable != null && pageable.getSort().get().count() != 0;
    }

    public static Pageable setHistorySort(Pageable pageable) {
        return setSort(pageable, historySort());
    }

    public static <T> Page<T> buildEmptyPage() {
        return new PageImpl<>(Collections.emptyList());
    }

    public static <T> Pageable applyEntitySort(Pageable pageable, Class<T> dtoClass) {
        return applyEntitySort(pageable, dtoClass, DefaultEntity.class);
    }

    public static <T, E> Pageable applyEntitySort(Pageable pageable, Class<T> dtoClass, Class<E> entityClass) {
        var sort = resolveSort(pageable, dtoClass);
        if (sort.isEmpty()) {
            return pageable;
        }
        var mappedSort = mapToEntitySort(sort.get(), dtoClass, entityClass);

        return setSort(pageable, mappedSort);
    }

    private static <T> Optional<Sort> resolveSort(Pageable pageable, Class<T> dtoClass) {
        if (hasSort(pageable)) {
            return Optional.of(pageable.getSort());
        } else {
            return findDefaultSort(dtoClass);
        }
    }

    public static <T> Optional<Sort> findDefaultEntitySort(Class<T> dtoClass) {
        return findDefaultSort(dtoClass).map(sort -> mapToEntitySort(sort, dtoClass, DefaultEntity.class));
    }

    private static <T, E> Sort mapToEntitySort(Sort sort, Class<T> dtoClass, Class<E> entitySort) {
        var mappedSort = sort.get()
                .flatMap(order -> mapToEntitySortOrder(order, dtoClass, entitySort))
                .distinct()
                .collect(Collectors.toList());
        return Sort.by(mappedSort);
    }

    private static <T, E> Stream<Sort.Order> mapToEntitySortOrder(Sort.Order order, Class<T> dtoClass, Class<E> entityClass) {
        var entitySorts = findMatchingEntitySorts(order, dtoClass, entityClass)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(entitySorts)) {
            return entitySorts.stream()
                    .map(PaginationUtils::toEntityProperty)
                    .map(s -> new Sort.Order(order.getDirection(), s, order.getNullHandling()));
        }

        if (!entityClass.equals(DefaultEntity.class)) {
            //try to find entity sorts with unspecified entity class
            entitySorts = findMatchingEntitySorts(order, dtoClass, DefaultEntity.class)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(entitySorts)) {
                return entitySorts.stream()
                        .map(PaginationUtils::toEntityProperty)
                        .map(s -> new Sort.Order(order.getDirection(), s, order.getNullHandling()));
            }
        }

        return Stream.of(order);
    }

    private static <T, E> Stream<EntitySort> findMatchingEntitySorts(Sort.Order order, Class<T> dtoClass, Class<E> entityClass) {
        var property = order.getProperty();
        var field = ReflectionUtils.findField(dtoClass, property);
        if (field != null) {
            if (field.isAnnotationPresent(EntitySort.List.class)) {
                return Stream.of(field.getAnnotation(EntitySort.List.class).value())
                        .filter(entitySort -> entitySort.entity().equals(entityClass));
            }
            if (field.isAnnotationPresent(EntitySort.class)) {
                return Stream.of(field.getAnnotation(EntitySort.class))
                        .filter(entitySort -> entitySort.entity().equals(entityClass));
            }
        }
        if (dtoClass.getSuperclass() != null && dtoClass.getSuperclass() != Object.class) {
            return findMatchingEntitySorts(order, dtoClass.getSuperclass(), entityClass);
        }

        return Stream.empty();
    }

    private static String toEntityProperty(EntitySort entitySort) {
        if (StringUtils.isNotEmpty(entitySort.value())) {
            return entitySort.value();
        }
        return StringUtils.join(entitySort.joined(), ".");
    }

    private static <T> Optional<Sort> findDefaultSort(Class<T> dtoClass) {
        Map<String, Field> map = new HashMap<>();
        Class<?> i = dtoClass;
        while (i != null && i != Object.class) {
            for (int j = 0; j < i.getDeclaredFields().length; j++) {
                if (!map.containsKey(i.getDeclaredFields()[j].getName())) {
                    map.put(i.getDeclaredFields()[j].getName(), i.getDeclaredFields()[j]);
                }
            }
            i = i.getSuperclass();
        }
        var orders = map.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(field -> field.isAnnotationPresent(DefaultSort.class))
                .sorted(Comparator.comparingInt(value -> value.getAnnotation(DefaultSort.class).order()))
                .map(f -> new Sort.Order(f.getAnnotation(DefaultSort.class).direction(), f.getName()))
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Sort.by(orders));
    }
}
