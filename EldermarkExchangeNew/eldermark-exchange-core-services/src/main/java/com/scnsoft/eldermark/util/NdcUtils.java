package com.scnsoft.eldermark.util;

public class NdcUtils {

    private NdcUtils() {
    }

    public static String normalize(String ndcCode) {
        return ndcCode.replaceAll("-", "");
    }

    public static String toDisplayValue(String ndcCode) {
        if (ndcCode.length() != 11) {
            throw new IllegalArgumentException("Invalid normalized NDC code");
        }
        return ndcCode.substring(0, 5) + "-" + ndcCode.substring(5, 9) + "-" + ndcCode.substring(9, 11);
    }
}
