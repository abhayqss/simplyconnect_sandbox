package com.scnsoft.eldermark.shared;

import com.scnsoft.eldermark.services.connect.ConnectUtil;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SearchScope")
@XmlEnum(String.class)
public enum SearchScope {
    @XmlEnumValue("ELDERMARK")
    ELDERMARK("ELDERMARK", ConnectUtil.EXCHANGE_HCID),

    @XmlEnumValue("NWHIN")
    NWHIN("NWHIN", "1.2.840.114350.1.13.8.3.7.3.688884.100");

    private String code;
    private String homeCommunityId;

    private SearchScope(String code, String homeCommunityId) {
        this.code = code;
        this.homeCommunityId = homeCommunityId;
    }

    public String getCode() {
        return code;
    }

    public String getHomeCommunityId() {
        return homeCommunityId;
    }
}
