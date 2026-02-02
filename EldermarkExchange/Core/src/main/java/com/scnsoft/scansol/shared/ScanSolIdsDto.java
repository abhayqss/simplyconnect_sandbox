package com.scnsoft.scansol.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Date: 15.05.15
 * Time: 11:06
 */
@JsonSerialize (include=JsonSerialize.Inclusion.NON_NULL)
public class ScanSolIdsDto {
    @JsonProperty("ids")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
