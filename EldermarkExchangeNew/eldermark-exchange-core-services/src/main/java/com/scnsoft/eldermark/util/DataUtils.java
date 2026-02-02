package com.scnsoft.eldermark.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.*;

public class DataUtils {
    private static final Logger logger = LoggerFactory.getLogger(DataUtils.class);

    public static boolean hasData(Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof CharSequence) {
            return hasData((CharSequence) o);
        }
        if (o instanceof Iterable) {
            return hasData((Iterable) o);
        }

        if (!o.getClass().getPackageName().startsWith("com.scnsoft.eldermark")) {
            return true; //ignore non-application classes
        }

        //do recursive check of fields
        var fields = o.getClass().getDeclaredFields();

        for (var field : fields) {
            if (field.isAnnotationPresent(DataPresenceCheckIgnore.class)) {
                continue;
            }
            try {
                field.setAccessible(true);
                var fieldValue = field.get(o);
                field.setAccessible(false);

                if (hasData(fieldValue)) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                logger.warn("Couldn't get field value", e);
            }
        }
        return false;
    }

    public static boolean hasData(CharSequence cs) {
        return StringUtils.isNotBlank(cs);
    }

    public static boolean hasData(Iterable<?> it) {
        if (it == null) {
            return false;
        }

        for (var o : it) {
            if (hasData(o)) {
                return true;
            }
        }
        return false;
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DataPresenceCheckIgnore {

    }
}
