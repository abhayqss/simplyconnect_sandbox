package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import org.apache.commons.codec.binary.Base64;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

public class Utils {

    public static String createCommaSeparatedList(Object... listItems) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listItems.length; i++) {
            sb.append(listItems[i].toString());
            if (i != listItems.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String duplicateInACommaSeparatedList(Object valueToDuplicate, int numberOfDuplications) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfDuplications; i++) {
            sb.append(valueToDuplicate.toString());
            if (i != numberOfDuplications - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String getStacktraceAsString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public static <I> List<I> getIds(List<? extends IdentifiableSourceEntity<I>> sourceEntities) {
        List<I> ids = new ArrayList<I>();
        for (IdentifiableSourceEntity<I> sourceEntity : sourceEntities) {
            I id = sourceEntity.getId();
            if (id == null) {
                throw new IllegalStateException("id is null");
            }
            ids.add(id);
        }
        return ids;
    }

    public static <I> List<String> getStringIds(List<? extends IdentifiableSourceEntity<I>> sourceEntities) {
        List<String> ids = new ArrayList<String>();
        for (IdentifiableSourceEntity<I> sourceEntity : sourceEntities) {
            I id = sourceEntity.getId();
            if (id == null) {
                throw new IllegalStateException("id is null");
            }
            ids.add(String.valueOf(id));
        }
        return ids;
    }

    public static void ensureNotNull(Object parameter, String parameterName) {
        if (parameter == null) {
            throw new NullPointerException(parameterName + " cannot be null");
        }
    }

    public static void ensureNotNullNotEmpty(Collection<?> collection, String parameterName) {
        ensureNotNull(collection, parameterName);
        if (collection.size() == 0) {
            throw new IllegalArgumentException(parameterName + " cannot be empty");
        }
    }

    public static void ensureNotNullNotEmpty(Object[] parametersArray, String parameterName) {
        ensureNotNull(parametersArray, parameterName);
        if (parametersArray.length == 0) {
            throw new IllegalArgumentException(parameterName + " cannot be empty");
        }
    }

    public static void ensurePositive(double parameter, String parameterName) {
        if (parameter <= 0) {
            throw new IllegalArgumentException(parameterName + " must be positive");
        }
    }

    public static void ensureNotNegative(double parameter, String parameterName) {
        if (parameter < 0) {
            throw new IllegalArgumentException(parameterName + " cannot be negative");
        }
    }

    public static Date convertZeroDateToNull(ResultSet rs, String columnName) throws SQLException {
        try {
            return rs.getDate(columnName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Time convertZeroTimeToNull(ResultSet rs, String columnName) throws SQLException {
        try {
            return rs.getTime(columnName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static java.util.Date mergeDateTime(Date date, Time time) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);

        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        dateCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));

        return dateCal.getTime();
    }

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrZero(Long number) {
        return number == null || number == 0;
    }

    public static String base64Encode(String text) {
        try {
            return Base64.encodeBase64String(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to base64 encode text: '" + text + "'", e);
        }
    }

    public static <T> List<List<T>> partitionList(List<T> list, int partitionSize) {
        ensurePositive(partitionSize, "partitionSize");

        List<List<T>> result = new ArrayList<List<T>>();

        int startIndex = 0;
        int endIndex;
        while (startIndex < list.size()) {
            endIndex = startIndex + partitionSize;
            if (endIndex > list.size()) {
                endIndex = list.size();
            }
            result.add(list.subList(startIndex, endIndex));
            startIndex = endIndex;
        }

        return result;
    }

    public static boolean isStaticField(Field field) {
        return java.lang.reflect.Modifier.isStatic(field.getModifiers());
    }

    public static void makeFieldsAccessible(Collection<Field> fields) {
        for (Field field : fields) {
            field.setAccessible(true);
        }
    }

    public static String ensureLeadingPlusInPhoneNumberExists(String phoneNumber) {
        String result = phoneNumber;
        if (!isEmpty(result) && !result.startsWith("+")) {
            result = "+" + result;
        }
        return result;
    }

    public static <T> List<String> convertToStringList(List<T> sourceList) {
        if (sourceList == null) {
            return null;
        }
        List<String> result = new ArrayList<String>(sourceList.size());
        for (T item : sourceList) {
            result.add(item.toString());
        }
        return result;
    }
}
