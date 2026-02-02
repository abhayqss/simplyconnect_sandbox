package com.scnsoft.eldermark.framework.model.source;

import com.scnsoft.eldermark.framework.Utils;

import java.lang.reflect.Field;

public abstract class SourceEntity {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" {");
        Field[] fields = getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!Utils.isStaticField(field)) {
                field.setAccessible(true);
                String name = field.getName();
                Object value;
                try {
                    value = field.get(this);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                sb.append(name).append("='").append(value).append("'");
                if (i != fields.length - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("}");

        return sb.toString();
    }
}
