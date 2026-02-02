package com.scnsoft.eldermark.entity;


import java.util.Optional;
import java.util.stream.Stream;

public enum PersonTelecomCode {
    EMAIL(0),
    WP(1),  /*Office*/
    HP(2),  /*Home*/
    FAX(5),
    MC(6),  /*Mobile*/
    EC(7),  /*Emergency*/
    UN(8);  /*Unknown*/

    private final int code;

    PersonTelecomCode(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Optional<PersonTelecomCode> fromName(String name) {
        return Stream.of(PersonTelecomCode.values())
                .filter(c -> c.name().equals(name))
                .findFirst();
    }
}
