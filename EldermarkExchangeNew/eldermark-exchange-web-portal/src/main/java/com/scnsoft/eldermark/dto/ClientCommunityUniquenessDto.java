package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientCommunityUniquenessDto {

    private Boolean ssn;
    private Boolean medicareNumber;
    private Boolean medicaidNumber;
    private Boolean memberNumber;

    public ClientCommunityUniquenessDto(Boolean ssn, Boolean medicareNumber, Boolean medicaidNumber, Boolean memberNumber) {
        this.ssn = ssn;
        this.medicareNumber = medicareNumber;
        this.medicaidNumber = medicaidNumber;
        this.memberNumber = memberNumber;
    }

    public Boolean getSsn() {
        return ssn;
    }

    public Boolean getMedicareNumber() {
        return medicareNumber;
    }

    public Boolean getMedicaidNumber() {
        return medicaidNumber;
    }

    public Boolean getMemberNumber() {
        return memberNumber;
    }
}
