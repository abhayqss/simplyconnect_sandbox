package com.scnsoft.eldermark.services.password;

import com.scnsoft.eldermark.dao.password.DatabasePasswordSettingsDao;
import com.scnsoft.eldermark.dao.password.PasswordHistoryDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.entity.password.PasswordHistory;
import com.scnsoft.eldermark.entity.password.PasswordSettingsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordHistoryServiceImpl implements PasswordHistoryService {

    @Autowired
    private PasswordHistoryDao passwordHistoryDao;

    @Autowired
    private DatabasePasswordSettingsDao databasePasswordSettingsDao;

    @Override
    public void clearPasswordHistory(Long databaseId) {
        passwordHistoryDao.clearPasswordHistory(databaseId);
    }

    @Override
    public List<PasswordHistory> findAllByEmployeeId(Long employeeId) {
        return passwordHistoryDao.findAllByEmployeeId(employeeId);
    }

    @Override
    public void enablePasswordHistory(Long databaseId) {
        passwordHistoryDao.enablePasswordHistory(databaseId);
    }

    @Override
    public void addCurrentPasswordToHistoryIfEnabled(Employee employee) {
        DatabasePasswordSettings pwdHistorySetting =  databasePasswordSettingsDao.getOrganizationSpecificSetting(employee.getDatabaseId(), PasswordSettingsType.COMPLEXITY_PASSWORD_HISTORY_COUNT);
        if(pwdHistorySetting != null && pwdHistorySetting.getEnabled() && pwdHistorySetting.getValue() > 0) {
            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setEmployee(employee);
            passwordHistory.setPassword(employee.getPassword());
            passwordHistoryDao.save(passwordHistory);
        }
    }
}
