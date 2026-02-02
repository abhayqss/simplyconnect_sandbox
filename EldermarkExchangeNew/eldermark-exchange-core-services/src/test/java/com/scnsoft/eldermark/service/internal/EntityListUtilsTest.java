package com.scnsoft.eldermark.service.internal;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EntityListUtilsTest {

    @Test
    void displayNameComparatorTest1() {
        var unsortedDisplayNames = Stream.of("c", "b", "a");

        var sortedList = unsortedDisplayNames
                .map(s -> new DisplayableNamedEntity(null, s))
                .sorted(EntityListUtils.displayNameComparator("b"))
                .collect(Collectors.toList());

        assertEquals("a", sortedList.get(0).getDisplayName());
        assertEquals("c", sortedList.get(1).getDisplayName());
        assertEquals("b", sortedList.get(2).getDisplayName());
    }

    @Test
    void displayNameComparatorTest2() {
        var unsortedDisplayNames = Stream.of("c", "b", "a");

        var sortedList = unsortedDisplayNames
                .map(s -> new DisplayableNamedEntity(null, s))
                .sorted(EntityListUtils.displayNameComparator("a"))
                .collect(Collectors.toList());

        assertEquals("b", sortedList.get(0).getDisplayName());
        assertEquals("c", sortedList.get(1).getDisplayName());
        assertEquals("a", sortedList.get(2).getDisplayName());
    }
}
