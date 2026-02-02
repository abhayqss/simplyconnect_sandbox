package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.List;

public class PasswordSettingsDto {
    private Long organizationId;
    List<DatabasePasswordSettings> databasePasswordSettingsList;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<DatabasePasswordSettings> getDatabasePasswordSettingsList() {
        return databasePasswordSettingsList;
    }

    public void setDatabasePasswordSettingsList(List<DatabasePasswordSettings> databasePasswordSettingsList) {
        this.databasePasswordSettingsList = databasePasswordSettingsList;
    }
}
