package com.scnsoft.eldermark.entity.phr;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Type of Vital Sign
 * Created by averazub on 1/6/2017.
 */
public enum VitalSignType {
    RESP("9279-1", "Respiration Rate"),
    HEART_BEAT("8867-4", "Heart Beat"),
    O2_SAT("2710-2", "Oxygen Saturation"),
    INTR_SYSTOLIC("8480-6", "Intravascular Systolic"),
    INTR_DIASTOLIC("8462-4", "Intravascular Diastolic"),
    TEMP("8310-5", "Body Temperature"),
    HEIGHT("8302-2", "Body Height"),
    HEIGHT_LYING("8306-3", "Body Height (Lying)"),
    CIRCUMFERENCE("8287-5", "Circumference Occipital-Frontal (Tape Measure)"),
    WEIGHT("3141-9", "Body Weight");

    VitalSignType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    private final String code;
    private final String displayName;

    public String code() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public String displayName() {
        return displayName;
    }

    public static VitalSignType getByCode(String code) {
        for (VitalSignType type: VitalSignType.values()) {
            if (type.code().equals(code)) return type;
        }
        return null;
    }

    public static Collection<String> supportedCodes() {
        List<String> codes = new ArrayList<String>();
        CollectionUtils.collect(Arrays.asList(values()), new BeanToPropertyValueTransformer("code"), codes);
        return codes;
    }

    /*
    code	code_system	display_name

     */
}
