package com.scnsoft.eldermark.service.password;

import com.scnsoft.eldermark.dao.password.OrganizationPasswordSettingsDao;
import com.scnsoft.eldermark.dao.password.PasswordSettingsDao;
import com.scnsoft.eldermark.entity.password.OrganizationPasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrganizationPasswordSettingsServiceImpl implements OrganizationPasswordSettingsService {

    @Autowired
    private OrganizationPasswordSettingsDao organizationPasswordSettingsDao;

    @Autowired
    private PasswordSettingsDao passwordSettingsDao;

    @Override
    public List<OrganizationPasswordSettings> getOrganizationPasswordSettings(Long organizationId) {
        return organizationPasswordSettingsDao.findAllByOrganizationId(organizationId);
    }

    @Override
    public List<OrganizationPasswordSettings> getOrganizationPasswordSettings(Long organizationId, boolean enabled) {
        return organizationPasswordSettingsDao.findAllByOrganizationIdAndEnabled(organizationId, enabled);
    }

    @Override
    public List<OrganizationPasswordSettings> createDefaultOrganizationPasswordSettings(Long organizationId) {
        List<OrganizationPasswordSettings> defaultPasswordSettings = createDefaultSettings(organizationId);
        return organizationPasswordSettingsDao.saveAll(defaultPasswordSettings);
    }

    private List<OrganizationPasswordSettings> createDefaultSettings(Long organizationId) {
        List<OrganizationPasswordSettings> result = new ArrayList<>();
        List<PasswordSettings> allPasswordSettings = passwordSettingsDao.findAll();
        for (PasswordSettings passwordSettings : allPasswordSettings) {
            OrganizationPasswordSettings databasePasswordSettings = new OrganizationPasswordSettings();
            databasePasswordSettings.setPasswordSettings(passwordSettings);
            databasePasswordSettings.setOrganizationId(organizationId);
            switch (passwordSettings.getPasswordSettingsType()) {
                case ACCOUNT_NUMBER_OF_FAILED_LOGINS_ALLOWED_COUNT: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(5L);
                    break;
                }
                case ACCOUNT_LOCK_IN_MINUTES:
                case ACCOUNT_RESET_FAILED_LOGON_COUNT_IN_MINUTES: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(15L);
                    break;
                }
                case COMPLEXITY_PASSWORD_LENGTH: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(8L);
                    break;
                }
                case COMPLEXITY_UPPERCASE_COUNT:
                case COMPLEXITY_LOWERCASE_COUNT:
                case COMPLEXITY_NON_ALPHANUMERIC_COUNT:
                case COMPLEXITY_ARABIC_NUMERALS_COUNT:
                case COMPLEXITY_LESS_SPACES_THAN: {
                    databasePasswordSettings.setEnabled(Boolean.TRUE);
                    databasePasswordSettings.setValue(1L);
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
                    databasePasswordSettings.setValue(0L);
                    break;
                }
            }
            result.add(databasePasswordSettings);
        }
        return result;
    }
}
