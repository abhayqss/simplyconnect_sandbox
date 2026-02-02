package com.scnsoft.eldermark.exchange.fk;

import java.util.HashSet;
import java.util.Set;

public class GroupRoleForeignKeys {
    private Set<Long> roleIds;

    public GroupRoleForeignKeys() {
        roleIds = new HashSet<Long>();
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void addRoleId(Long roleId) {
        if (roleId != null) {
            roleIds.add(roleId);
        }
    }
}
