package com.scnsoft.eldermark.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author phomal
 * Created on 10/25/2017.
 */
public class MockitoMatchers {

    public static <T> Matcher<List<T>> sameAsSet(final List<T> expectedList) {
        return new BaseMatcher<List<T>>() {
            @Override
            public boolean matches(Object o) {
                List<T> actualList;
                try {
                    actualList = (List<T>) o;
                } catch (ClassCastException e) {
                    return false;
                }
                Set<T> expectedSet = new HashSet<>(expectedList);
                Set<T> actualSet = new HashSet<>(actualList);
                return actualSet.equals(expectedSet);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("should contain all and only elements of ").appendValue(expectedList);
            }
        };
    }

}
