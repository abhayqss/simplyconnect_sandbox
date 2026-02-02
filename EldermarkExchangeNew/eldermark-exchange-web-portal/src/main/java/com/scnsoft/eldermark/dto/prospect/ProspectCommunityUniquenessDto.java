package com.scnsoft.eldermark.dto.prospect;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProspectCommunityUniquenessDto {

    private Boolean ssn;

    public ProspectCommunityUniquenessDto(Boolean ssn) {
        this.ssn = ssn;
    }

    public Boolean getSsn() {
        return ssn;
    }
}
