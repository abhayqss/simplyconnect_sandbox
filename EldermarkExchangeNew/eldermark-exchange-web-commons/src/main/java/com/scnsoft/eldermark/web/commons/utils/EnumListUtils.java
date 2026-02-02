package com.scnsoft.eldermark.web.commons.utils;

import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;
import org.apache.commons.lang.StringUtils;

import java.util.List;


public final class EnumListUtils {

    public static <T extends NamedTitledEntityDto> void moveItemToEnd(List<T> enums, final String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        for (T t : enums) {
            if (t.getTitle().equalsIgnoreCase(value)) {
                enums.remove(t);
                enums.add(t);
                return;
            }
        }
    }
}
