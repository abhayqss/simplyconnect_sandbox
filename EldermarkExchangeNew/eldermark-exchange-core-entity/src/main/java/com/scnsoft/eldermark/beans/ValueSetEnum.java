package com.scnsoft.eldermark.beans;


import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public enum ValueSetEnum {
    PROCEDURE_REASON("2.16.840.1.113883.4.642.3.432"),
    PROBLEM_ACT_STATUS_CODE("2.16.840.1.113883.11.20.9.19"),
    PROBLEM_SEVERITY("2.16.840.1.113883.3.88.12.3221.6.8"),
    PROBLEM_STATUS("2.16.840.1.113883.3.88.12.80.68"),
    PROBLEM_TYPE_2006("2.16.840.1.113883.1.11.20.14"),
    ADVERSE_EVENT_TYPE("2.16.840.1.113883.3.88.12.3221.6.2"),

    CURRENT_SMOKING_STATUS("2.16.840.1.113883.11.20.9.38", Arrays.asList(
            new ValueSetCode("449868002", CodeSystem.SNOMED_CT),
            new ValueSetCode("428041000124106", CodeSystem.SNOMED_CT),
            new ValueSetCode("8517006", CodeSystem.SNOMED_CT),
            new ValueSetCode("266919005", CodeSystem.SNOMED_CT),
            new ValueSetCode("77176002", CodeSystem.SNOMED_CT),
            new ValueSetCode("266927001", CodeSystem.SNOMED_CT),
            new ValueSetCode("428071000124103", CodeSystem.SNOMED_CT),
            new ValueSetCode("428061000124105", CodeSystem.SNOMED_CT)
    )),

    ETHNIC_GROUP("2.16.840.1.114222.4.11.837",
            Arrays.asList(
                    new ValueSetCode("2186-5", CodeSystem.RACE_AND_ETHNICITY_CDC),
                    new ValueSetCode("2135-2", CodeSystem.RACE_AND_ETHNICITY_CDC)
            )
    ),

    GENDER("2.16.840.1.113883.1.11.1"),
    MARITAL_STATUS("2.16.840.1.113883.1.11.12212"),
    RACE("2.16.840.1.113883.1.11.14914");


    private final String oid;

    //another way to represent codes belonging to relatively small ValueSets.
    //This approach allows to avoid dealing with database if the codes already exist
    //and there are duplicates (m.b with different display names).
    //Ideally we should deduplicate all CcdCodes in the future and find a way to
    //handle InterpretiveCcdCode better.
    private final List<ValueSetCode> codes;

    ValueSetEnum(String oid) {
        this(oid, null);
    }

    ValueSetEnum(String oid, List<ValueSetCode> codes) {
        this.oid = oid;
        this.codes = codes;
    }

    public String getOid() {
        return oid;
    }

    public boolean isFromValueSet(String code, String codeSystem) {
        return CollectionUtils.emptyIfNull(this.codes).stream()
                .anyMatch(c -> c.code.equals(code)
                        && c.codeSystem.getOid().equals(codeSystem));
    }

    public static class ValueSetCode {
        private final String code;
        private final CodeSystem codeSystem;

        public ValueSetCode(String code, CodeSystem codeSystem) {
            this.code = code;
            this.codeSystem = codeSystem;
        }
    }
}
