package com.scnsoft.eldermark.dump.util;

import org.apache.commons.collections4.IterableUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils {

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

//    public static <T, A extends IdAware> Collector<T, ?, Map<A, List<T>>> groupingByIdOf(Function<? super T, ? extends A> classifier) {
//        return Collectors.groupingBy(classifier,
//                () -> new TreeMap<>(Comparator.comparingLong(A::getId)),
//                Collectors.toList());
//    }

    public static<T, A> Collector<T, ?, Map<A, T>> toMapOfUniqueKeys(Function<? super T, ? extends A> uniqueKeyExtractor) {
        return toMapOfUniqueKeysAndThen(uniqueKeyExtractor, Function.identity());
    }

    public static<T, A, Q> Collector<T, ?, Map<A, Q>> toMapOfUniqueKeysAndThen(Function<? super T, ? extends A> uniqueKeyExtractor,
                                                                            Function<? super T, ? extends Q> downstreamValueMapper) {
        return Collectors.groupingBy(uniqueKeyExtractor,
                Collectors.collectingAndThen(
                        Collectors.reducing((c1, c2) -> c1),
                        o -> downstreamValueMapper.apply(o.orElseThrow()))
        );
    }

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(IterableUtils.emptyIfNull(iterable).spliterator(), false);
    }

//    public static <T extends IdAware> Collector<T, ?, Set<T>> toIdsComparingSet() {
//        return Collectors.toCollection(CareCoordinationUtils::idsComparingSet);
//    }
}
