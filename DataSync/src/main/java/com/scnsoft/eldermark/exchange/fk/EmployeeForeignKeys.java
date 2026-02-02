package com.scnsoft.eldermark.exchange.fk;

import java.util.HashSet;
import java.util.Set;

public class EmployeeForeignKeys {
    private Set<Long> groupIds;

    public EmployeeForeignKeys() {
        groupIds = new HashSet<Long>();
    }

    public Set<Long> getGroupIds() {
        return groupIds;
    }

    public void addGroupId(Long groupId) {
        if (groupId != null) {
            groupIds.add(groupId);
        }
    }
}
