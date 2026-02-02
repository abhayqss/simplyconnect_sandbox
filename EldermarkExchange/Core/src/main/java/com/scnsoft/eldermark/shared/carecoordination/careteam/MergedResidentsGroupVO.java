package com.scnsoft.eldermark.shared.carecoordination.careteam;

import java.util.Set;

public class MergedResidentsGroupVO {
    private Set<Long> mergedResidentIds;

    public MergedResidentsGroupVO(Set<Long> mergedResidentIds) {
        this.mergedResidentIds = mergedResidentIds;
    }

    public Boolean containsResidentId(Long residentId) {
        return mergedResidentIds.contains(residentId);
    }

    public Set<Long> getMergedResidentIds() {
        return mergedResidentIds;
    }

}
