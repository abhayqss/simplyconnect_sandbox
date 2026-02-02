package com.scnsoft.eldermark.shared.administration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * @author phomal
 * Created on 4/5/2017.
 */
@XmlType(name = "MatchStatus")
@XmlEnum(String.class)
public enum MatchStatus {

    @XmlEnumValue("SURELY_MATCHED")
    SURELY_MATCHED,
    @XmlEnumValue("MAYBE_MATCHED")
    MAYBE_MATCHED,
    @XmlEnumValue("NOT_MATCHED")
    NOT_MATCHED

}
