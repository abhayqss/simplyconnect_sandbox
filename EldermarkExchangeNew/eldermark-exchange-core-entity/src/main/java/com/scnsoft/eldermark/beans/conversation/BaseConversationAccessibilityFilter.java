package com.scnsoft.eldermark.beans.conversation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.annotations.InternalFilterParameter;

public class BaseConversationAccessibilityFilter {

    @InternalFilterParameter
    @JsonIgnore
    private Long excludedEmployeeId;

    public Long getExcludedEmployeeId() {
        return excludedEmployeeId;
    }

    public void setExcludedEmployeeId(Long excludedEmployeeId) {
        this.excludedEmployeeId = excludedEmployeeId;
    }
}
