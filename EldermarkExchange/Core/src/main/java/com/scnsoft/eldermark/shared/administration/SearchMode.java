package com.scnsoft.eldermark.shared.administration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Search mode
 * Created on 3/31/17.
 *
 * @author phomal
 * @see com.scnsoft.eldermark.shared.ResidentFilter
 */
@XmlType(name = "SearchMode")
@XmlEnum(String.class)
public enum SearchMode {

    /**
     * Search records by exact match of all fields
     */
    @XmlEnumValue("MATCH_ALL")
    MATCH_ALL,

    /**
     * Search records by exact match of any field
     */
    @XmlEnumValue("MATCH_ANY")
    MATCH_ANY,

    /**
     * Search records containing specified values in any specified field.<br/>
     * IMPLEMENTATION NOTES: currently only First Name, Last Name, Community Name, Provider Organization Name, and SSN are considered in this mode.
     */
    @XmlEnumValue("MATCH_ANY_LIKE")
    MATCH_ANY_LIKE

}
