package com.scnsoft.eldermark.shared.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author phomal
 * Created on 5/31/2017.
 */
public class SqlConstraintMsgMappingResolver {

    private static Map<String, PhrExceptionType> map = new HashMap<>();
    static {
        map.put("UQ_UserMobile_email_normalized_database", PhrExceptionType.DUPLICATED_EMAIL);
    }

    public static PhrExceptionType map(String constraintName) {
        return map.get(constraintName);
    }

}
