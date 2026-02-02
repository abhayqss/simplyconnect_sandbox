package com.scnsoft.eldermark.api.shared.dto;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;

/**
 * Created by pzhurba on 15-Dec-15.
 */
public class CareTeamRoleDto extends KeyValueDto {
    private CareTeamRoleCode code;

    public CareTeamRoleDto(Long id, String label, CareTeamRoleCode code) {
        super(id, label);
        this.code = code;
    }

    public CareTeamRoleCode getCode() {
        return code;
    }

    public void setCode(CareTeamRoleCode code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CareTeamRoleDto that = (CareTeamRoleDto) o;

        return getCode() == that.getCode();

    }

    @Override
    public int hashCode() {
        return getCode() != null ? getCode().hashCode() : 0;
    }
}
