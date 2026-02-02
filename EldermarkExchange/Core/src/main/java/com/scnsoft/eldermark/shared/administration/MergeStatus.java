package com.scnsoft.eldermark.shared.administration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * @author phomal
 * Created on 4/5/2017.
 */
@XmlType(name = "MergeStatus")
@XmlEnum(String.class)
public enum MergeStatus {

    @XmlEnumValue("MERGED_AUTOMATICALLY")
    MERGED_AUTOMATICALLY,
    @XmlEnumValue("MERGED_MANUALLY")
    MERGED_MANUALLY,
    @XmlEnumValue("NOT_MERGED")
    NOT_MERGED

}
