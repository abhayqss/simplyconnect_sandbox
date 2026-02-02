package com.scnsoft.eldermark.service.internal;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedEntity;
import com.scnsoft.eldermark.entity.basic.DisplayablePrimaryFocusAwareEntity;

public final class EntityListUtils {

    public static <T extends DisplayableNamedEntity> void moveItemToEnd(List<T> entities, final String displayName) {
        if (StringUtils.isEmpty(displayName)) {
            return;
        }
        for (T t : entities) {
            if (t.getDisplayName().equalsIgnoreCase(displayName)) {
                entities.remove(t);
                entities.add(t);
                return;
            }
        }
    }

    /**
     * @param endingDisplayName Items with this display name will be at the end
     */
    public static <T extends DisplayableNamedEntity> Comparator<T> displayNameComparator(String endingDisplayName) {
        if (endingDisplayName == null) {
            return Comparator.comparing(DisplayableNamedEntity::getDisplayName);
        } else {
            return Comparator.comparing(
                    DisplayableNamedEntity::getDisplayName,
                    (s1, s2) -> {
                        if (endingDisplayName.equals(s1)) {
                            return s1.equals(s2) ? 0 : 1;
                        } else if (endingDisplayName.equals(s2)) {
                            return -1;
                        } else {
                            return Comparator.<String>naturalOrder().compare(s1, s2);
                        }
                    }
            );
        }
    }

    public static <T extends DisplayableNamedEntity> void moveItemToStart(List<T> entities, final String displayName) {
        if (StringUtils.isEmpty(displayName)) {
            return;
        }
        for (T t : entities) {
            if (t.getDisplayName().equalsIgnoreCase(displayName)) {
                entities.remove(t);
                entities.add(0, t);
                return;
            }
        }
    }

    public static <T extends DisplayablePrimaryFocusAwareEntity> void moveItemToEndFromMapofLists(Map<Long, List<T>> entityMap, final String displayName) {
        if (StringUtils.isEmpty(displayName)) {
            return;
        }
        for (List<T> list : entityMap.values()) {
            moveItemToEnd(list, displayName);
        }
    }


}
