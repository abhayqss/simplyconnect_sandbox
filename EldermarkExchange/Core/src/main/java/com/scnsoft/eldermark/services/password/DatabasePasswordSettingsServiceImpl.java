package com.scnsoft.eldermark.services.password;

import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.password.DatabasePasswordSettingsDao;
import com.scnsoft.eldermark.dao.password.PasswordSettingsDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import com.scnsoft.eldermark.shared.carecoordination.DatabasePasswordSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabasePasswordSettingsServiceImpl implements DatabasePasswordSettingsService {

    @Autowired
    private DatabasePasswordSettingsDao databasePasswordSettingsDao;

    @Autowired
    private PasswordSettingsDao passwordSettingsDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public List<DatabasePasswordSettings> getOrganizationPasswordSettings(Long organizationId) {
        return databasePasswordSettingsDao.getOrganizationPasswordSettings(organizationId);
    }

    @Override
    public List<DatabasePasswordSettings> createDefaultDatabasePasswordSettings(Long organizationId) {
        List<DatabasePasswordSettings> result = new ArrayList<DatabasePasswordSettings>();
        List<DatabasePasswordSettings> defaultPasswordSettings = getDefaultSettings(organizationId);
        for (DatabasePasswordSettings entity : defaultPasswordSettings) {
            result.add(databasePasswordSettingsDao.create(entity));
        }
        return result;
    }

    @Override
    public void updateDatabasePasswordSettings(List<DatabasePasswordSettings> databasePasswordSettings) {
        databasePasswordSettingsDao.updateDatabasePasswordSettings(databasePasswordSettings);
    }

    @Override
    public List<DatabasePasswordSettingsDto> getOrganizationPasswordSettingsDtoForEmployee(String loginName, String loginCompanyId) {
        List<DatabasePasswordSettingsDto> result = new ArrayList<DatabasePasswordSettingsDto>();
        Employee employee = employeeDao.getActiveEmployee(loginName, loginCompanyId);
        if (employee != null) {
            List<DatabasePasswordSettings> databasePasswordSettings = getOrganizationPasswordSettings(employee.getDatabaseId());
            if (!CollectionUtils.isEmpty(databasePasswordSettings)) {
                for (DatabasePasswordSettings dps : databasePasswordSettings) {
                    result.add(convert(dps));
                }
            }
        }
        return result;
    }

    @Override
    public DatabasePasswordSettings getOrganizationSpecificSetting(Long organizationId, PasswordSettingsType passwordSettingsType) {
        return databasePasswordSettingsDao.getOrganizationSpecificSetting(organizationId, passwordSettingsType);
    }

    @Override
    public List<DatabasePasswordSettingsDto> getOrganizationPasswordSettingsDto(Long organizationId) {
        List<DatabasePasswordSettingsDto> result = new ArrayList<DatabasePasswordSettingsDto>();
        List<DatabasePasswordSettings> databasePasswordSettings = getOrganizationPasswordSettings(organizationId);
        if (!CollectionUtils.isEmpty(databasePasswordSettings)) {
            for (DatabasePasswordSettings dps : databasePasswordSettings) {
                result.add(convert(dps));
            }
        }
        return result;
    }

    private static DatabasePasswordSettingsDto convert(DatabasePasswordSettings dps) {
        final DatabasePasswordSettingsDto databasePasswordSettingsDto = new DatabasePasswordSettingsDto();
        databasePasswordSettingsDto.setId(dps.getId());
        databasePasswordSettingsDto.setEnabled(dps.getEnabled());
        databasePasswordSettingsDto.setValue(dps.getValue());
        databasePasswordSettingsDto.setPasswordSettingsType(dps.getPasswordSettings().getPasswordSettingsType());
        return databasePasswordSettingsDto;
    }

    private List<DatabasePasswordSettings> getDefaultSettings(Long organizationId) {
        List<DatabasePasswordSettings> result = new ArrayList<DatabasePasswordSettings>();
        List<PasswordSettings> allPasswordSettings = passwordSettingsDao.findAll();
        for (PasswordSettings passwordSettings : allPasswordSettings) {
            DatabasePasswordSettings databasePasswordSettings = new DatabasePasswordSettings();
            databasePasswordSettings.setPasswordSettings(passwordSettings);
            databasePasswordSettings.setDatabaseId(organizationId);
            switch (passwordSettings.getPasswordSettingsType()) {
                case ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(5l);
                    break;
                }
                case ACCOUNT_LOCK_IN_MINUTES:
                case ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(15l);
                    break;
                }
                case COMPLEXITY_PASSWORD_LENGTH: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(8l);
                    break;
                }
                case COMPLEXITY_UPPERCASE_COUNT:
                case COMPLEXITY_LOWERCASE_COUNT:
                case COMPLEXITY_NON_ALPHANUMERIC_COUNT:
                case COMPLEXITY_ARABIC_NUMERALS_COUNT:
                case COMPLEXITY_LESS_SPACES_THAN:{
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(1l);
                    break;
                }
                case PASSWORD_MAXIMUM_AGE_IN_DAYS: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(90L);
                    break;
                }
                case COMPLEXITY_PASSWORD_HISTORY_COUNT: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(8L);
                    break;
                }
                default: {
                    databasePasswordSettings.setEnabled(Boolean.FALSE);
                    databasePasswordSettings.setValue(0l);
                    break;
                }
            }
            result.add(databasePasswordSettings);
        }
        return result;
    }
}
