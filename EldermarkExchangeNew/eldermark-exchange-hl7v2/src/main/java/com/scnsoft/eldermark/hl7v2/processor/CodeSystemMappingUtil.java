package com.scnsoft.eldermark.hl7v2.processor;


import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CodeSystemMappingUtil {

    private static Map<String, CodeSystem> codeSystemAdditionalMapping = Map.of(
            "I10", CodeSystem.ICD_10_CM,
            "I10C", CodeSystem.ICD_10_CM,
            "ICD10", CodeSystem.ICD_10_CM,
            "I9", CodeSystem.ICD_9_CM,
            "I9C", CodeSystem.ICD_9_CM,
            "ICD9", CodeSystem.ICD_9_CM
    );

    private CodeSystemMappingUtil() {
        codeSystemAdditionalMapping = new HashMap<>();
    }


    public static Optional<CodeSystem> resolveCodeSystem(String hl7CodeSystemName) {
        if (StringUtils.isEmpty(hl7CodeSystemName)) {
            return Optional.empty();
        }
        var upper = hl7CodeSystemName.toUpperCase();
        return Stream.of(CodeSystem.values())
                .filter(codeSystem -> codeSystem.name().equals(upper))
                .findFirst()
                .or(() -> Optional.ofNullable(codeSystemAdditionalMapping.getOrDefault(upper, null)));
    }
}
