package com.scnsoft.eldermark.consana.sync.server.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IdentifierCode {

    SSN("SS"),
    MRN("MR"),
    MEDICAID("MA"),
    MEDICARE("MC"),
    MEMBER_NUMBER("MB");

    private String code;

}
