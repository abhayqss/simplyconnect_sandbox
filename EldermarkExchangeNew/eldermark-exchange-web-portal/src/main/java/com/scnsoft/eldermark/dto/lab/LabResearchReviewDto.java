package com.scnsoft.eldermark.dto.lab;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class LabResearchReviewDto {

    @NotEmpty
    private Set<Long> orderIds;

    public Set<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(Set<Long> orderIds) {
        this.orderIds = orderIds;
    }
}
