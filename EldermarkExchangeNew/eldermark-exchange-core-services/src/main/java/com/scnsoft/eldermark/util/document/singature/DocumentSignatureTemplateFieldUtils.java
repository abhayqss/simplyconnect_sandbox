package com.scnsoft.eldermark.util.document.singature;

import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DocumentSignatureTemplateFieldUtils {

    private DocumentSignatureTemplateFieldUtils() {
    }

    public static Map<String, Object> flattenFieldValues(Map<?, ?> fieldValues) {

        var result = new HashMap<String, Object>();

        // Using foreach instead of collect because collect to map doesn't support nul values
        flattenFieldValues("", fieldValues)
                .forEach(it -> result.put(it.getFirst(), it.getSecond()));

        return result;
    }

    private static Stream<Pair<String, Object>> flattenFieldValues(String prefix, Map<?, ?> fieldValues) {

        if (MapUtils.isEmpty(fieldValues)) {
            return Stream.empty();
        }

        return fieldValues.entrySet().stream()
                .flatMap(entry -> {
                    if (!(entry.getKey() instanceof String)) {
                        throw new BusinessException("Invalid field values");
                    }
                    var fieldName = prefix + entry.getKey();
                    var fieldValue = entry.getValue();

                    if (fieldValue instanceof Map) {
                        return flattenFieldValues(fieldName + ".", (Map<?, ?>) fieldValue);
                    } else {
                        return Stream.of(Pair.of(fieldName, fieldValue));
                    }
                });
    }

    public static Map<String, Object> makeFieldValueTree(List<Pair<String, Object>> fields) {
        var result = new HashMap<String, Object>();
        fields.forEach(entry -> {
            var flatFieldName = entry.getFirst();
            var fieldValue = entry.getSecond();

            if (flatFieldName.contains(".")) {
                putComposedFieldToMap(result, flatFieldName, fieldValue);
            } else {
                putFlatFieldToMap(result, flatFieldName, fieldValue);
            }
        });

        return result;
    }

    private static void putComposedFieldToMap(HashMap<String, Object> result, String flatFieldName, Object fieldValue) {
        Map<String, Object> map = result;

        var fieldNameParts = flatFieldName.split("\\.");
        var lastNamePartIndex = fieldNameParts.length - 1;
        for (int i = 0; i < lastNamePartIndex; i++) {
            var fieldNamePart = fieldNameParts[i];

            var subMap = map.computeIfAbsent(fieldNamePart, (k) -> new HashMap<String, Object>());
            if (subMap instanceof Map) {
                map = (Map<String, Object>) subMap;
            } else {
                throw new IllegalStateException("Invalid template fields: composed field name is invalid");
            }
        }
        var lastNamePart = fieldNameParts[lastNamePartIndex];
        putFlatFieldToMap(map, lastNamePart, fieldValue);
    }

    private static void putFlatFieldToMap(Map<String, Object> result, String flatFieldName, Object fieldValue) {
        if (result.containsKey(flatFieldName)) {
            throw new IllegalStateException("Invalid template fields: fields have the same names");
        }
        result.put(flatFieldName, fieldValue);
    }
}
