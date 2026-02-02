package com.scnsoft.eldermark.entity;

/**
 * Created by pzhurba on 29-Sep-15.
 */
public enum PersonTelecomCode {
    EMAIL(0),
    WP(1),  /*Office*/
    HP(2),  /*Home*/
    FAX(5),
    MC(6);  /*Mobile*/

    private final int code;

    PersonTelecomCode(final int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
