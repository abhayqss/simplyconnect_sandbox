package com.scnsoft.eldermark.entity;

/**
 * Postal address types<br/>
 * See <a href="http://www.cdapro.com/know/25062">CDA Pro - The addr element</a>
 *
 * @author phomal
 * Created on 5/29/2017.
 */
public enum PersonAddressCode {
    H(0),   /*Home address*/
    HP(1),  /*Primary home address*/
    HV(2),  /*Vacation home address*/
    WP(3),  /*Workplace address*/
    DIR(4),  /*Direct workplace address*/
    PUB(5),  /*Public workplace address*/
    BAD(6),  /*Bad address*/
    TMP(7);  /*Temporary address*/

    private final int code;

    PersonAddressCode(final int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
